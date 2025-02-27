#!/bin/bash

# 1. 检查是否传入了参数
if [ $# -ne 1 ]; then
    echo "Usage: $0 <dst_chain_id>"
    exit 1
fi

# 2. 获取传入的参数
DST_CHAIN_ID=$1

# 3. 定义变量
H2CHAIN_DIR=~/CIPS-Gemini-H2Chain
CONFIG_FILE="$H2CHAIN_DIR/config.yml"
LOG_DIR="$H2CHAIN_DIR/logs"
CURRENT_TIME=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/h2chain_$CURRENT_TIME.log"

# 4. 创建日志目录
mkdir -p "$LOG_DIR"

# 5. 进入指定目录
cd "$H2CHAIN_DIR" || { echo "目录不存在"; exit 1; }

# 6. 备份原文件
cp "$CONFIG_FILE" "${CONFIG_FILE}.bak"

# 7. 使用sed命令替换目标链ID
sed -i "/^test:/,/^[^ ]/ s/dst_chain_id: [0-9]*/dst_chain_id: $DST_CHAIN_ID/" "$CONFIG_FILE"

# 8. 检查替换是否成功
echo "检查更新后的内容："
grep -A 4 "^test:" "$CONFIG_FILE"

# 9. 启动H2Chain网关并记录日志
echo "启动H2Chain网关..."
nohup ./crossH2C start > "$LOG_FILE" 2>&1 &
H2CHAIN_PID=$!

# 10. 等待网关启动
echo "等待网关启动（5秒）..."
sleep 5

# 11. 检查网关是否成功启动
if ! ps -p $H2CHAIN_PID > /dev/null; then
    echo "H2Chain网关启动失败，请检查日志文件"
    mv "${CONFIG_FILE}.bak" "$CONFIG_FILE"
    exit 1
fi

# 12. 删除备份文件
rm -f "${CONFIG_FILE}.bak"

# 13. 提示用户脚本执行完成
echo "H2Chain配置完成："
echo "- 目标链ID已更新为: $DST_CHAIN_ID"
echo "- 网关已在后台启动（PID: $H2CHAIN_PID）"
echo "- 日志文件：$LOG_FILE"
echo
echo "开始监听日志..."
# 使用tail -f监听日志
tail -f "$LOG_FILE"

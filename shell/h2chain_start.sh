#!/bin/bash

# 0. 加载环境变量
source /etc/profile
source ~/.bashrc

# 1. 检查是否传入了参数
if [ $# -ne 1 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 参数数量不正确"
    echo "Usage: $0 <dst_chain_id>"
    echo "Received parameters: $@"
    exit 1
fi

# 2. 获取传入的参数
DST_CHAIN_ID=$1

# 3. 定义变量
H2CHAIN_DIR=/root/CIPS-Gemini-H2Chain
CONFIG_FILE="$H2CHAIN_DIR/config.yml"
LOG_DIR="$H2CHAIN_DIR/logs"
CURRENT_TIME=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/h2chain_$CURRENT_TIME.log"

echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 接收到的参数：DST_CHAIN_ID=$DST_CHAIN_ID"
echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 环境变量："
echo "H2CHAIN_DIR=$H2CHAIN_DIR"
echo "CONFIG_FILE=$CONFIG_FILE"
echo "PATH=$PATH"
echo "GOPATH=$GOPATH"
echo "GOROOT=$GOROOT"

# 4. 创建日志目录
mkdir -p "$LOG_DIR"
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 创建日志目录失败"
    exit 1
fi

# 5. 进入指定目录
cd "$H2CHAIN_DIR"
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 进入目录 $H2CHAIN_DIR 失败"
    echo "当前目录: $(pwd)"
    exit 1
fi

echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 当前工作目录: $(pwd)"

# 6. 备份原文件
cp "$CONFIG_FILE" "${CONFIG_FILE}.bak"
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 备份配置文件失败"
    exit 1
fi

# 7. 使用sed命令替换目标链ID
echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 开始更新配置文件"
sed -i "/^test:/,/^[^ ]/ s/dst_chain_id: [0-9]*/dst_chain_id: $DST_CHAIN_ID/" "$CONFIG_FILE"
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 更新配置文件失败"
    mv "${CONFIG_FILE}.bak" "$CONFIG_FILE"
    exit 1
fi

# 8. 启动H2Chain网关并记录日志
echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 开始启动海河链网关..."

# 检查环境
which go >> "$LOG_FILE" 2>&1
go version >> "$LOG_FILE" 2>&1

nohup ./crossH2C start > "$LOG_FILE" 2>&1 &
H2CHAIN_PID=$!

echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 启动进程 PID: $H2CHAIN_PID"

# 9. 等待网关启动
sleep 5

# 10. 检查网关是否成功启动
if ! ps -p $H2CHAIN_PID > /dev/null; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 海河链网关启动失败"
    echo "查看启动日志："
    tail -n 50 "$LOG_FILE"
    mv "${CONFIG_FILE}.bak" "$CONFIG_FILE"
    exit 1
fi

# 11. 删除备份文件
rm -f "${CONFIG_FILE}.bak"

# 12. 输出启动信息
echo "[$(date '+%Y-%m-%d %H:%M:%S')] SUCCESS: 海河链网关已启动"
echo "PID: $H2CHAIN_PID"
echo "LOG: $LOG_FILE"
echo "DST_CHAIN_ID: $DST_CHAIN_ID"

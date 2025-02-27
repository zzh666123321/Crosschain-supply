#!/bin/bash

# 1. 检查是否传入了参数
if [ $# -ne 3 ]; then
    echo "Usage: $0 <chain_id> <ip> <port>"
    exit 1
fi

# 2. 获取传入的参数
CHAIN_ID=$1
IP=$2
PORT=$3

# 3. 定义变量
REPO_DIR=~/CIPS-Gemini-ChainMaker
TARGET_FILE="$REPO_DIR/relayer/tests/test_transport.go"
NEW_URL="http://$IP:$PORT"
LOG_DIR="$REPO_DIR/logs"
CURRENT_TIME=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/chainmaker_$CURRENT_TIME.log"

# 4. 创建日志目录
mkdir -p "$LOG_DIR"

# 5. 进入指定目录
cd "$REPO_DIR" || { echo "目录不存在"; exit 1; }

# 6. 备份原文件
cp "$TARGET_FILE" "${TARGET_FILE}.bak"

# 7. 使用sed命令精确替换指定行
# 替换第25行的 TARGET_CHAIN_ID
sed -i "25s/TARGET_CHAIN_ID int64 = [0-9]*/TARGET_CHAIN_ID int64 = $CHAIN_ID/" "$TARGET_FILE"

# 替换第32行的 TARGET_SERVER_URL
sed -i '32s|"http://[^"]*"|"'"$NEW_URL"'"|' "$TARGET_FILE"

# 8. 检查替换是否成功
echo "检查更新后的内容："
sed -n '25p;32p' "$TARGET_FILE"

# 9. 执行go run命令并记录日志
echo "启动长安链网关..."
nohup go run main.go source > "$LOG_FILE" 2>&1 &
CHAINMAKER_PID=$!

# 10. 等待网关启动
echo "等待网关启动（5秒）..."
sleep 5

# 11. 检查网关是否成功启动
if ! ps -p $CHAINMAKER_PID > /dev/null; then
    echo "长安链网关启动失败，请检查日志文件"
    mv "${TARGET_FILE}.bak" "$TARGET_FILE"
    exit 1
fi

# 12. 删除备份文件
rm -f "${TARGET_FILE}.bak"

# 13. 提示用户脚本执行完成
echo "长安链配置完成："
echo "- TARGET_CHAIN_ID 已更新为: $CHAIN_ID"
echo "- TARGET_SERVER_URL 已更新为: $NEW_URL"
echo "- 网关已在后台启动（PID: $CHAINMAKER_PID）"
echo "- 日志文件：$LOG_FILE"
echo
echo "开始监听日志..."
# 使用tail -f监听日志
tail -f "$LOG_FILE"

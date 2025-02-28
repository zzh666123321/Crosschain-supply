#!/bin/bash

# 1. 检查是否传入了参数
if [ $# -ne 3 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 参数数量不正确" >> /root/shell/chainmaker_debug.log
    echo "Usage: $0 <chain_id> <ip> <port>" >> /root/shell/chainmaker_debug.log
    echo "Received parameters: $@" >> /root/shell/chainmaker_debug.log
    exit 1
fi

# 2. 获取传入的参数
CHAIN_ID=$1
IP=$2
PORT=$3

echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 接收到的参数：CHAIN_ID=$CHAIN_ID, IP=$IP, PORT=$PORT" >> /root/shell/chainmaker_debug.log

# 3. 定义变量
REPO_DIR=/root/CIPS-Gemini-ChainMaker
TARGET_FILE="$REPO_DIR/relayer/tests/test_transport.go"
NEW_URL="http://$IP:$PORT"
LOG_DIR="$REPO_DIR/logs"
CURRENT_TIME=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/chainmaker_$CURRENT_TIME.log"

echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 环境变量：" >> /root/shell/chainmaker_debug.log
echo "REPO_DIR=$REPO_DIR" >> /root/shell/chainmaker_debug.log
echo "TARGET_FILE=$TARGET_FILE" >> /root/shell/chainmaker_debug.log
echo "NEW_URL=$NEW_URL" >> /root/shell/chainmaker_debug.log
echo "PATH=$PATH" >> /root/shell/chainmaker_debug.log
echo "GOPATH=$GOPATH" >> /root/shell/chainmaker_debug.log
echo "GOROOT=$GOROOT" >> /root/shell/chainmaker_debug.log

# 4. 创建日志目录
mkdir -p "$LOG_DIR"
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 创建日志目录失败" >> /root/shell/chainmaker_debug.log
    exit 1
fi

# 5. 进入指定目录
cd "$REPO_DIR"
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 进入目录 $REPO_DIR 失败" >> /root/shell/chainmaker_debug.log
    echo "当前目录: $(pwd)" >> /root/shell/chainmaker_debug.log
    exit 1
fi

echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 当前工作目录: $(pwd)" >> /root/shell/chainmaker_debug.log

# 6. 备份原文件
cp "$TARGET_FILE" "${TARGET_FILE}.bak"
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 备份文件失败" >> /root/shell/chainmaker_debug.log
    exit 1
fi

# 7. 使用sed命令精确替换指定行
echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 开始更新配置文件" >> /root/shell/chainmaker_debug.log

# 替换第25行的 TARGET_CHAIN_ID
sed -i "25s/TARGET_CHAIN_ID int64 = [0-9]*/TARGET_CHAIN_ID int64 = $CHAIN_ID/" "$TARGET_FILE"
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 更新 CHAIN_ID 失败" >> /root/shell/chainmaker_debug.log
    mv "${TARGET_FILE}.bak" "$TARGET_FILE"
    exit 1
fi

# 替换第32行的 TARGET_SERVER_URL
sed -i '32s|"http://[^"]*"|"'"$NEW_URL"'"|' "$TARGET_FILE"
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 更新 URL 失败" >> /root/shell/chainmaker_debug.log
    mv "${TARGET_FILE}.bak" "$TARGET_FILE"
    exit 1
fi

# 8. 检查替换是否成功
echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 更新后的配置内容：" >> /root/shell/chainmaker_debug.log
sed -n '25p;32p' "$TARGET_FILE" >> /root/shell/chainmaker_debug.log

# 9. 启动长安链网关并记录日志
echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 开始启动长安链网关..." >> /root/shell/chainmaker_debug.log

# 检查 Go 环境
which go >> /root/shell/chainmaker_debug.log 2>&1
go version >> /root/shell/chainmaker_debug.log 2>&1

nohup go run main.go source > "$LOG_FILE" 2>&1 &
CHAINMAKER_PID=$!

echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 启动进程 PID: $CHAINMAKER_PID" >> /root/shell/chainmaker_debug.log

# 10. 等待网关启动
sleep 5

# 11. 检查网关是否成功启动
if ! ps -p $CHAINMAKER_PID > /dev/null; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 长安链网关启动失败" >> /root/shell/chainmaker_debug.log
    echo "查看启动日志：" >> /root/shell/chainmaker_debug.log
    tail -n 50 "$LOG_FILE" >> /root/shell/chainmaker_debug.log
    mv "${TARGET_FILE}.bak" "$TARGET_FILE"
    exit 1
fi

# 12. 删除备份文件
rm -f "${TARGET_FILE}.bak"

# 13. 输出启动信息
echo "[$(date '+%Y-%m-%d %H:%M:%S')] SUCCESS: 长安链网关已启动" >> /root/shell/chainmaker_debug.log
echo "PID: $CHAINMAKER_PID" >> /root/shell/chainmaker_debug.log
echo "LOG: $LOG_FILE" >> /root/shell/chainmaker_debug.log
echo "CHAIN_ID: $CHAIN_ID" >> /root/shell/chainmaker_debug.log
echo "TARGET_URL: $NEW_URL" >> /root/shell/chainmaker_debug.log

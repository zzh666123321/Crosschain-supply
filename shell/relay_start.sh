#!/bin/bash

# 1. 定义变量
RELAY_DIR=/root/CIPS-Gemini-RelayChain
LOG_DIR="$RELAY_DIR/logs"
CURRENT_TIME=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/relay_$CURRENT_TIME.log"

# 2. 创建日志目录
mkdir -p "$LOG_DIR"

# 3. 进入指定目录
cd "$RELAY_DIR" || { echo "目录不存在: $RELAY_DIR"; exit 1; }

# 4. 启动中继链网关（在后台运行）
echo "启动中继链网关..."
nohup go run main.go start > "$LOG_FILE" 2>&1 &
RELAY_PID=$!

# 5. 等待网关启动
sleep 5

# 6. 检查网关是否成功启动
if ! ps -p $RELAY_PID > /dev/null; then
    echo "ERROR: 中继链网关启动失败"
    echo "请查看日志文件: $LOG_FILE"
    exit 1
fi

# 7. 执行注册流程
echo "执行注册流程..."
go run main.go register >> "$LOG_FILE" 2>&1

# 8. 输出启动信息
echo "SUCCESS: 中继链网关已启动"
echo "PID: $RELAY_PID"
echo "LOG: $LOG_FILE"

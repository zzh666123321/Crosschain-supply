#!/bin/bash

# 1. 定义变量
RELAY_DIR=~/CIPS-Gemini-RelayChain
LOG_DIR="$RELAY_DIR/logs"
CURRENT_TIME=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/relay_$CURRENT_TIME.log"

# 2. 创建日志目录
mkdir -p "$LOG_DIR"

# 3. 进入指定目录
cd "$RELAY_DIR" || { echo "目录不存在"; exit 1; }

# 4. 启动中继链网关（在后台运行）
echo "启动中继链网关..."
nohup go run main.go start > "$LOG_FILE" 2>&1 &
RELAY_PID=$!

# 5. 等待网关启动
echo "等待网关启动（5秒）..."
sleep 5

# 6. 检查网关是否成功启动
if ! ps -p $RELAY_PID > /dev/null; then
    echo "中继链网关启动失败，请检查日志文件"
    exit 1
fi

# 7. 执行注册流程并记录到同一个日志文件
echo "执行注册流程..."
go run main.go register >> "$LOG_FILE" 2>&1

# 8. 提示用户脚本执行完成
echo "中继链配置完成："
echo "- 网关已在后台启动（PID: $RELAY_PID）"
echo "- 注册流程已执行"
echo "- 日志文件：$LOG_FILE"
echo
echo "开始监听日志..."
# 使用tail -f监听日志
tail -f "$LOG_FILE"

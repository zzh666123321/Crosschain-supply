#!/bin/bash

# 1. 定义变量
REPO_DIR=~/CIPS-Gemini-Ethereum
LOG_DIR="$REPO_DIR/logs"
CURRENT_TIME=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/eth_$CURRENT_TIME.log"
START_RELAY_CMD="./helper.sh start_relayer config.toml"

# 2. 创建日志目录
mkdir -p "$LOG_DIR"

# 3. 进入指定目录
cd "$REPO_DIR" || { echo "目录不存在"; exit 1; }

# 4. 执行启动网关命令并记录日志
echo "启动以太坊网关..."
nohup $START_RELAY_CMD > "$LOG_FILE" 2>&1 &
ETH_PID=$!

# 5. 等待网关启动
echo "等待网关启动（5秒）..."
sleep 5

# 6. 检查网关是否成功启动
if ! ps -p $ETH_PID > /dev/null; then
    echo "以太坊网关启动失败，请检查日志文件"
    exit 1
fi

# 7. 提示用户网关已启动
echo "以太坊网关配置完成："
echo "- 网关已在后台启动（PID: $ETH_PID）"
echo "- 配置文件：config.toml"
echo "- 日志文件：$LOG_FILE"
echo
echo "开始监听日志..."
# 使用tail -f监听日志
tail -f "$LOG_FILE"

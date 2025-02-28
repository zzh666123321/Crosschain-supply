#!/bin/bash

# 0. 加载环境变量
source /etc/profile
source ~/.bashrc

# 1. 定义变量
REPO_DIR="/root/CIPS-Gemini-Ethereum"
LOG_DIR="$REPO_DIR/logs"
CURRENT_TIME=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$LOG_DIR/eth_$CURRENT_TIME.log"

echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 环境变量："
echo "REPO_DIR=$REPO_DIR"
echo "PATH=$PATH"
echo "GOPATH=$GOPATH"
echo "GOROOT=$GOROOT"

# 2. 创建日志目录
mkdir -p "$LOG_DIR"
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 创建日志目录失败"
    exit 1
fi

# 3. 进入指定目录
cd "$REPO_DIR"
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 进入目录 $REPO_DIR 失败"
    echo "当前目录: $(pwd)"
    exit 1
fi

echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 当前工作目录: $(pwd)"

# 4. 检查必要文件是否存在
if [ ! -f "helper.sh" ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: helper.sh 文件不存在"
    exit 1
fi

if [ ! -f "config.toml" ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: config.toml 文件不存在"
    exit 1
fi

# 5. 确保helper.sh有执行权限
chmod +x helper.sh

# 6. 执行启动网关命令并记录日志
echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 开始启动以太坊网关..."

# 检查环境
which go >> "$LOG_FILE" 2>&1
go version >> "$LOG_FILE" 2>&1

./helper.sh start_relayer config.toml >> "$LOG_FILE" 2>&1 &
ETH_PID=$!

echo "[$(date '+%Y-%m-%d %H:%M:%S')] INFO: 启动进程 PID: $ETH_PID"

# 7. 等待网关启动
sleep 5

# 8. 检查网关是否成功启动
if ! ps -p $ETH_PID > /dev/null; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] ERROR: 以太坊网关启动失败"
    echo "查看启动日志："
    tail -n 50 "$LOG_FILE"
    exit 1
fi

# 9. 输出启动信息
echo "[$(date '+%Y-%m-%d %H:%M:%S')] SUCCESS: 以太坊网关已启动"
echo "PID: $ETH_PID"
echo "LOG: $LOG_FILE" 
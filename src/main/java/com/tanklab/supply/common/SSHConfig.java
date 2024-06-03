package com.tanklab.supply.common;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class SSHConfig {

    public static boolean do_crosschain = true;// 是否进行跨链，目前测试阶段填否
    public static String ssh_username = "root";
    public static String ssh_ip = "116.204.36.31";
    public static int ssh_port = 22;
    public static String ssh_password = "ABCabc123";

    private static Session session = null;
    private static int timeout = 60000;
    private static ChannelExec channelExec;

    public static void connect(String ip) throws Exception {
        // System.out.println("连接到服务器:"+ssh_ip+",
        // 登录名为："+ssh_username+",端口号为："+ssh_port);
        Channel channel = null;
        JSch jsch = new JSch(); // 创建jsch 对象
        // 连接服务器，如果端口小于等于0，采用默认端口，如果大于0，使用指定的端口

        // 采用指定的端口连接服务器
        session = jsch.getSession(ssh_username, ip, ssh_port);

        // 如果服务器连不上，则抛出异常
        if (session == null) {
            throw new Exception("session is null");
        }
        // 设置登录主机的密码
        session.setPassword(ssh_password);
        // 设置第一次登录的时候的提示，可选择(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        session.setConfig(sshConfig);
        // 设置登陆超时时间ms
        session.setTimeout(timeout);
        session.connect();
        // System.out.println("Session connected.");
        // System.out.println("Opening Channel.");

    }

    /**
     * 在远程服务器上执行命令
     * 
     * @param cmd     要执行的命令字符串
     * @param charset 编码
     * @throws Exception
     */
    public static String executeCMD(String cmd, String charset) throws Exception {
        String logs = "";

        channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(cmd);
        channelExec.setInputStream(null);
        channelExec.setErrStream(System.err);
        channelExec.connect();

        InputStream in = channelExec.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName(charset)));
        String buf = "";
        while ((buf = reader.readLine()) != null) {
            logs += buf;
            System.out.println(buf);
            logs += "\n";
            // Date date = new Date();
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            // String formattedDate = sdf.format(date);
            // System.out.println(formattedDate);
        }
        reader.close();
        channelExec.disconnect();
        session.disconnect();
        return logs;
    }

    /*
     * public static void main(String[] args) throws Exception {
     * // Date date = new Date();
     * // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
     * // String formattedDate = sdf.format(date);
     * // System.out.println(formattedDate);
     * connect();
     * // String log =
     * executeCMD("source ~/.profile && cd CIPS-ETHEREUM-private && go run *.go -c eth -r true -ap beefAccess -ag "
     * + "abcde121aecde12" +" -cd 12","UTF-8");
     * String log =
     * executeCMD("source ~/.profile && cd /root/CIPS-ETHEREUM-private && go run *.go -c eth2chainmaker -r true -ap beefAccess -ag tj11213w1a"
     * ,"UTF-8");
     * //
     * // Date date1 = new Date();
     * // SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
     * // String formattedDate1 = sdf.format(date1);
     * // System.out.println(formattedDate1);
     * //
     * //
     * //
     * //
     * // long delta = date1.getTime()-date.getTime();
     * // System.out.println(delta);
     * // connect();
     * // String log =
     * executeCMD("source ~/.profile && cd /root/CIPS-ETHEREUM-private && go run chainmakercontract/contractInvoke.go -cn BeefKing -cm sellbeef -ag 111222+content"
     * ,"utf-8");
     * 
     * 
     * // String log = executeCMD("netstat -ntlp","utf-8");
     * // System.out.println();
     * // String log1 = executeCMD("pwd && ls","UTF-8");
     * // System.out.println("--------");
     * // String[] l = log1.split("\n");
     * // System.out.println(l[l.length-1]);
     * // System.out.println("--------");
     * // execute("go run *.go -t 2 -i 0 -n [Tanklab,tanklab] -c SafeMath -m add"
     * ,"UTF-8");
     * }
     */
}

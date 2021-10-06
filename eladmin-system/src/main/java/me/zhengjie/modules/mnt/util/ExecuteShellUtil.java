/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.modules.mnt.util;

import cn.hutool.core.io.IoUtil;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Vector;

/**
 * 执行shell命令
 *
 * @author: ZhangHouYing
 * @date: 2019/8/10
 */
@Slf4j
public class ExecuteShellUtil {
	// 标准输出
	private Vector<String> stdout;

	// JSch https://blog.csdn.net/u010022051/article/details/52724660
	// JSch是Java Secure Channel的缩写。JSch是一个SSH2的纯Java实现。它允许你连接到一个SSH服务器，并且可以使用端口转发，X11转发，文件传输等，
	Session session;

	public ExecuteShellUtil(final String ipAddress, final String username, final String password,int port) {
		try {
			// 创建连接SSH服务器的对象
			JSch jsch = new JSch();
			// 设置 用户名、ip、端口号、密码、配置
			session = jsch.getSession(username, ipAddress, port);
			session.setPassword(password);
			// StrictHostKeyChecking https://blog.csdn.net/LTCM_SAKURA/article/details/108238752
			// 内网中常忽略安全检查，故no
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect(3000);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

	}

	public int execute(final String command) {
		int returnCode = 0;
		// 三种常用的shell的自动交互方法 https://www.cnblogs.com/evi10032/p/5455990.html
		// shell支持管道交互模式,故可使用jsch中ChannelShell，使用BufferedReader的readLine()读取执行结果
		ChannelShell channel = null;
		PrintWriter printWriter = null;
		BufferedReader input = null;
		stdout = new Vector<String>();
		try {
			// ChannelShell(ing交互)与ChannelExec(脚本交互)区别 https://blog.csdn.net/u013066244/article/details/70911585
			// 选择channel交互的方式
			channel = (ChannelShell) session.openChannel("shell");
			channel.connect();
			// 将程序端与远程shell端的输入输出端互相对接，再通过完成对接的入口输入命令，完成shell命令交互
			// IO概念：输入是将读取输入设备的信息到内存、输出是输入的数据写入到输出的设备上
			input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
			printWriter = new PrintWriter(channel.getOutputStream());
			printWriter.println(command);
			printWriter.println("exit");
			printWriter.flush();
			log.info("The remote command is: ");
			String line;
			while ((line = input.readLine()) != null) {
				stdout.add(line);
				System.out.println(line);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return -1;
		}finally {
			IoUtil.close(printWriter);
			IoUtil.close(input);
			if (channel != null) {
				channel.disconnect();
			}
		}
		return returnCode;
	}

	public void close(){
		if (session != null) {
			session.disconnect();
		}
	}

	public String executeForResult(String command) {
		execute(command);
		StringBuilder sb = new StringBuilder();
		for (String str : stdout) {
			sb.append(str);
		}
		return sb.toString();
	}

}

package com.battleship.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.battleship.gui.BattleshipGUI;

public class TCPConnectionHelper {

	private static TCPConnectionHelper instance;
	private ServerSocket srvsock = null;
	private Socket clientsock = null;
	private ObjectInputStream input = null;
	private ObjectOutputStream output = null;

	private TCPConnectionHelper() {

	}

	public static TCPConnectionHelper getInstance() {
		if (instance == null) {
			instance = new TCPConnectionHelper();
		}
		return instance;
	}

	/**
	 * Create server socket to host a game.
	 */
	private Socket CreateSocket() {
		try {
			this.srvsock = new ServerSocket(9050);
			clientsock = srvsock.accept();
			output = new ObjectOutputStream(clientsock.getOutputStream());
			input = new ObjectInputStream(clientsock.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return clientsock;
	}

	/**
	 * Create client socket to connect a game.
	 */
	private Socket ConnectToSocket(String ip, int port) {
		try {
			clientsock = new Socket(ip, port);
			output = new ObjectOutputStream(clientsock.getOutputStream());
			input = new ObjectInputStream(clientsock.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return clientsock;
	}

	/**
	 * Close sockets.
	 */
	public void CloseSocket() {
		try {
			clientsock.shutdownOutput();
			clientsock.shutdownInput();
			clientsock.close();
			if (srvsock != null) {
				srvsock.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start hosting or connect a hosted game.
	 */
	public boolean StartConnection(JFrame frmBattleship, String tempIP) {
		boolean result = true;
		Object[] options = { "Yes", "No" };
		if (tempIP.equals("")) {
			int n = JOptionPane.showOptionDialog(frmBattleship,
					"You are about to host a game on port 9050. Do you want to continue?", "Hosting Game",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
			if (n == 0) {
				CreateSocket();
			} else {
				result = false;
			}
		} else {
			boolean isValid = tempIP.matches("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})");
			if (isValid) {
				int n = JOptionPane.showOptionDialog(frmBattleship,
						String.format("You are about to connect a game on %s. Do you want to continue?", tempIP),
						"Connecting a Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
						options[1]);
				if (n == 0) {
					ConnectToSocket(tempIP.split(":")[0], Integer.parseInt(tempIP.split(":")[1]));
				} else {
					result = false;
				}
			} else {
				result = false;
				JOptionPane.showMessageDialog(frmBattleship,
						"Ip and port numbers are not in valid format (e.g. 128.168.1.1:8080).", "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
		}
		return result;
	}

	/**
	 * Send attack info to enemy.
	 */
	public void SendAttackInfo(BattleshipGUI gui, AttackInfo attackInfo) {
		AttackSender sender = new AttackSender(gui, input, output, attackInfo);
		sender.execute();
	}

	/**
	 * Receive attack info from enemy.
	 */
	public void ReceiveAttackInfo(BattleshipGUI gui) {
		AttackReceiver receiver = new AttackReceiver(gui, input, output);
		receiver.execute();
	}
}

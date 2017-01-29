package com.battleship.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.battleship.network.AttackInfo;
import com.battleship.network.AttackResult;
import com.battleship.network.TCPConnectionHelper;
import com.battleship.system.BattleshipHelper;
import com.battleship.system.Orientation;

public class BattleshipGUI {

	private JFrame frmBattleship;
	private JPanel panelShip;
	private JPanel panelStart;
	private JPanel panelBomb;
	private JPanel panelPlayerGrid;
	private JPanel panelEnemyGrid;
	private JTextField textIP;

	private BattleshipHelper bsHelper;
	private TCPConnectionHelper tcpHelper;

	private boolean isMyTurn = false;
	private String activeShipName = "";
	private byte activeShipSize = 0;
	private byte activeShipOrientation = Orientation.HORIZONTAL.getValue();
	private String activeBombName = "";
	private byte activeBombRadius = 0;
	private boolean activeBombInflictsDamage = false;
	private int killCount = 0;
	private int deadCount = 0;

	/**
	 * Launch application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BattleshipGUI window = new BattleshipGUI();
					window.frmBattleship.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create application.
	 */
	public BattleshipGUI() {
		bsHelper = BattleshipHelper.getInstance();
		tcpHelper = TCPConnectionHelper.getInstance();
		Initialise();
	}

	/**
	 * Initialise the contents of the frame.
	 */
	private void Initialise() {
		frmBattleship = new JFrame();
		frmBattleship.setResizable(false);
		frmBattleship.setTitle("Battleship");
		frmBattleship.setBounds(100, 100, 797, 535);
		frmBattleship.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBattleship.getContentPane().setLayout(null);

		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(395, 115, 2, 385);
		frmBattleship.getContentPane().add(separator);

		JLabel lblPlayerZone = new JLabel("Player Zone");
		lblPlayerZone.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayerZone.setBounds(160, 93, 75, 15);
		frmBattleship.getContentPane().add(lblPlayerZone);

		JLabel lblEnemyZone = new JLabel("Enemy Zone");
		lblEnemyZone.setHorizontalAlignment(SwingConstants.CENTER);
		lblEnemyZone.setBounds(557, 93, 75, 15);
		frmBattleship.getContentPane().add(lblEnemyZone);

		panelShip = new JPanel();
		panelShip.setBounds(0, 0, 280, 93);
		panelShip.setLayout(null);
		frmBattleship.getContentPane().add(panelShip);

		panelStart = new JPanel();
		panelStart.setBounds(0, 0, 506, 93);
		panelStart.setLayout(null);
		frmBattleship.getContentPane().add(panelStart);

		panelBomb = new JPanel();
		panelBomb.setBounds(0, 0, 787, 93);
		panelBomb.setLayout(null);
		frmBattleship.getContentPane().add(panelBomb);

		panelPlayerGrid = new JPanel();
		panelPlayerGrid.setBounds(0, 0, 390, 500);
		panelPlayerGrid.setLayout(null);
		frmBattleship.getContentPane().add(panelPlayerGrid);

		panelEnemyGrid = new JPanel();
		panelEnemyGrid.setBounds(0, 0, 787, 500);
		panelEnemyGrid.setLayout(null);
		frmBattleship.getContentPane().add(panelEnemyGrid);

		JButton btnCarrier = new JButton("Carrier (x1)");
		btnCarrier.setName("btnCarrier");
		btnCarrier.setToolTipText("5-square ship. Visible to probes.");
		btnCarrier.setBounds(10, 11, 125, 20);
		btnCarrier.setForeground(Color.DARK_GRAY);
		btnCarrier.addActionListener(shipListener);
		panelShip.add(btnCarrier);

		JButton btnCruiser = new JButton("Cruiser (x1)");
		btnCruiser.setName("btnCruiser");
		btnCruiser.setToolTipText("3-square ship. Visible to probes.");
		btnCruiser.setBounds(145, 11, 125, 20);
		btnCruiser.setForeground(Color.DARK_GRAY);
		btnCruiser.addActionListener(shipListener);
		panelShip.add(btnCruiser);

		JButton btnBattleship = new JButton("Battleship (x1)");
		btnBattleship.setName("btnBattleship");
		btnBattleship.setToolTipText("4-square ship. Visible to probes.");
		btnBattleship.setBounds(10, 37, 125, 20);
		btnBattleship.setForeground(Color.DARK_GRAY);
		btnBattleship.addActionListener(shipListener);
		panelShip.add(btnBattleship);

		JButton btnDestroyer = new JButton("Destroyer (x2)");
		btnDestroyer.setName("btnDestroyer");
		btnDestroyer.setToolTipText("2-square ship. Visible to probes.");
		btnDestroyer.setBounds(145, 37, 125, 20);
		btnDestroyer.setForeground(Color.DARK_GRAY);
		btnDestroyer.addActionListener(shipListener);
		panelShip.add(btnDestroyer);

		JButton btnSubmarine = new JButton("Submarine (x2)");
		btnSubmarine.setName("btnSubmarine");
		btnSubmarine.setToolTipText("3-square submarine. Invisible to probes.");
		btnSubmarine.setBounds(10, 63, 125, 20);
		btnSubmarine.setForeground(Color.DARK_GRAY);
		btnSubmarine.addActionListener(shipListener);
		panelShip.add(btnSubmarine);

		JButton btnRotate = new JButton("Rotate");
		btnRotate.setName("btnRotate");
		btnRotate.setToolTipText("Rotates orientation of the ship.");
		btnRotate.setBounds(145, 63, 125, 20);
		btnRotate.setForeground(Color.DARK_GRAY);
		btnRotate.addActionListener(rotateListener);
		panelShip.add(btnRotate);

		textIP = new JTextField();
		textIP.setName("textIP");
		textIP.setToolTipText("IPAddress:Port");
		textIP.setBounds(296, 11, 200, 20);
		textIP.setForeground(Color.DARK_GRAY);
		textIP.setColumns(10);
		panelStart.add(textIP);

		JButton btnStart = new JButton("START");
		btnStart.setName("btnStart");
		btnStart.setToolTipText(
				"<html><p width=\"350\">To connect another player's game, enter ip and port numbers. Otherwise, you will host a new game on port 9050.</p></html>");
		btnStart.setBounds(346, 36, 100, 45);
		btnStart.setForeground(Color.DARK_GRAY);
		btnStart.addActionListener(startListener);
		panelStart.add(btnStart);

		JButton btnFire = new JButton("Fire (x\u221E)");
		btnFire.setName("btnFire");
		btnFire.setToolTipText("Normal fire. Unlimited use. Destroys only selected square.");
		btnFire.setBounds(522, 11, 125, 20);
		btnFire.setForeground(Color.DARK_GRAY);
		btnFire.addActionListener(fireListener);
		btnFire.setEnabled(false);
		panelBomb.add(btnFire);

		JButton btnProbe = new JButton("Probe (x1)");
		btnProbe.setName("btnProbe");
		btnProbe.setToolTipText(
				"<html><p width=\"400\">Probe fire. Limited use. Reveals (doesn't destroy) selected square and 3-unit adjacent squares in straight directions. </br>Submarines can't be seen by the probe. Enemy doesn't know about the probed squares.</p></html>");
		btnProbe.setBounds(657, 11, 125, 20);
		btnProbe.setForeground(Color.DARK_GRAY);
		btnProbe.addActionListener(fireListener);
		btnProbe.setEnabled(false);
		panelBomb.add(btnProbe);

		JButton btnBomb = new JButton("Bomb (x2)");
		btnBomb.setName("btnBomb");
		btnBomb.setToolTipText(
				"<html><p width=\"400\">Small bomb. Limited use. Destroys selected square and 1-unit adjacent squares in straight directions.</p></html>");
		btnBomb.setBounds(522, 37, 125, 20);
		btnBomb.setForeground(Color.DARK_GRAY);
		btnBomb.addActionListener(fireListener);
		btnBomb.setEnabled(false);
		panelBomb.add(btnBomb);

		JButton btnHbomb = new JButton("H-Bomb (x1)");
		btnHbomb.setName("btnHbomb");
		btnHbomb.setToolTipText(
				"<html><p width=\"400\">Big bomb. Limited use. Destroys selected square and 2-unit adjacent squares in straight directions.</p></html>");
		btnHbomb.setBounds(657, 37, 125, 20);
		btnHbomb.setForeground(Color.DARK_GRAY);
		btnHbomb.addActionListener(fireListener);
		btnHbomb.setEnabled(false);
		panelBomb.add(btnHbomb);

		JButton btn1p1 = new JButton("");
		btn1p1.setBounds(10, 120, 25, 25);
		btn1p1.setName("btn1p1");
		btn1p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p1);

		JButton btn1p2 = new JButton("");
		btn1p2.setBounds(35, 120, 25, 25);
		btn1p2.setName("btn1p2");
		btn1p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p2);

		JButton btn1p3 = new JButton("");
		btn1p3.setBounds(60, 120, 25, 25);
		btn1p3.setName("btn1p3");
		btn1p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p3);

		JButton btn1p4 = new JButton("");
		btn1p4.setBounds(85, 120, 25, 25);
		btn1p4.setName("btn1p4");
		btn1p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p4);

		JButton btn1p5 = new JButton("");
		btn1p5.setBounds(110, 120, 25, 25);
		btn1p5.setName("btn1p5");
		btn1p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p5);

		JButton btn1p6 = new JButton("");
		btn1p6.setBounds(135, 120, 25, 25);
		btn1p6.setName("btn1p6");
		btn1p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p6);

		JButton btn1p7 = new JButton("");
		btn1p7.setBounds(160, 120, 25, 25);
		btn1p7.setName("btn1p7");
		btn1p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p7);

		JButton btn1p8 = new JButton("");
		btn1p8.setBounds(185, 120, 25, 25);
		btn1p8.setName("btn1p8");
		btn1p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p8);

		JButton btn1p9 = new JButton("");
		btn1p9.setBounds(210, 120, 25, 25);
		btn1p9.setName("btn1p9");
		btn1p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p9);

		JButton btn1p10 = new JButton("");
		btn1p10.setBounds(235, 120, 25, 25);
		btn1p10.setName("btn1p10");
		btn1p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p10);

		JButton btn1p11 = new JButton("");
		btn1p11.setBounds(260, 120, 25, 25);
		btn1p11.setName("btn1p11");
		btn1p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p11);

		JButton btn1p12 = new JButton("");
		btn1p12.setBounds(285, 120, 25, 25);
		btn1p12.setName("btn1p12");
		btn1p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p12);

		JButton btn1p13 = new JButton("");
		btn1p13.setBounds(310, 120, 25, 25);
		btn1p13.setName("btn1p13");
		btn1p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p13);

		JButton btn1p14 = new JButton("");
		btn1p14.setBounds(335, 120, 25, 25);
		btn1p14.setName("btn1p14");
		btn1p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p14);

		JButton btn1p15 = new JButton("");
		btn1p15.setBounds(360, 120, 25, 25);
		btn1p15.setName("btn1p15");
		btn1p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn1p15);

		JButton btn2p1 = new JButton("");
		btn2p1.setBounds(10, 145, 25, 25);
		btn2p1.setName("btn2p1");
		btn2p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p1);

		JButton btn2p2 = new JButton("");
		btn2p2.setBounds(35, 145, 25, 25);
		btn2p2.setName("btn2p2");
		btn2p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p2);

		JButton btn2p3 = new JButton("");
		btn2p3.setBounds(60, 145, 25, 25);
		btn2p3.setName("btn2p3");
		btn2p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p3);

		JButton btn2p4 = new JButton("");
		btn2p4.setBounds(85, 145, 25, 25);
		btn2p4.setName("btn2p4");
		btn2p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p4);

		JButton btn2p5 = new JButton("");
		btn2p5.setBounds(110, 145, 25, 25);
		btn2p5.setName("btn2p5");
		btn2p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p5);

		JButton btn2p6 = new JButton("");
		btn2p6.setBounds(135, 145, 25, 25);
		btn2p6.setName("btn2p6");
		btn2p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p6);

		JButton btn2p7 = new JButton("");
		btn2p7.setBounds(160, 145, 25, 25);
		btn2p7.setName("btn2p7");
		btn2p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p7);

		JButton btn2p8 = new JButton("");
		btn2p8.setBounds(185, 145, 25, 25);
		btn2p8.setName("btn2p8");
		btn2p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p8);

		JButton btn2p9 = new JButton("");
		btn2p9.setBounds(210, 145, 25, 25);
		btn2p9.setName("btn2p9");
		btn2p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p9);

		JButton btn2p10 = new JButton("");
		btn2p10.setBounds(235, 145, 25, 25);
		btn2p10.setName("btn2p10");
		btn2p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p10);

		JButton btn2p11 = new JButton("");
		btn2p11.setBounds(260, 145, 25, 25);
		btn2p11.setName("btn2p11");
		btn2p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p11);

		JButton btn2p12 = new JButton("");
		btn2p12.setBounds(285, 145, 25, 25);
		btn2p12.setName("btn2p12");
		btn2p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p12);

		JButton btn2p13 = new JButton("");
		btn2p13.setBounds(310, 145, 25, 25);
		btn2p13.setName("btn2p13");
		btn2p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p13);

		JButton btn2p14 = new JButton("");
		btn2p14.setBounds(335, 145, 25, 25);
		btn2p14.setName("btn2p14");
		btn2p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p14);

		JButton btn2p15 = new JButton("");
		btn2p15.setBounds(360, 145, 25, 25);
		btn2p15.setName("btn2p15");
		btn2p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn2p15);

		JButton btn3p1 = new JButton("");
		btn3p1.setBounds(10, 170, 25, 25);
		btn3p1.setName("btn3p1");
		btn3p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p1);

		JButton btn3p2 = new JButton("");
		btn3p2.setBounds(35, 170, 25, 25);
		btn3p2.setName("btn3p2");
		btn3p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p2);

		JButton btn3p3 = new JButton("");
		btn3p3.setBounds(60, 170, 25, 25);
		btn3p3.setName("btn3p3");
		btn3p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p3);

		JButton btn3p4 = new JButton("");
		btn3p4.setBounds(85, 170, 25, 25);
		btn3p4.setName("btn3p4");
		btn3p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p4);

		JButton btn3p5 = new JButton("");
		btn3p5.setBounds(110, 170, 25, 25);
		btn3p5.setName("btn3p5");
		btn3p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p5);

		JButton btn3p6 = new JButton("");
		btn3p6.setBounds(135, 170, 25, 25);
		btn3p6.setName("btn3p6");
		btn3p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p6);

		JButton btn3p7 = new JButton("");
		btn3p7.setBounds(160, 170, 25, 25);
		btn3p7.setName("btn3p7");
		btn3p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p7);

		JButton btn3p8 = new JButton("");
		btn3p8.setBounds(185, 170, 25, 25);
		btn3p8.setName("btn3p8");
		btn3p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p8);

		JButton btn3p9 = new JButton("");
		btn3p9.setBounds(210, 170, 25, 25);
		btn3p9.setName("btn3p9");
		btn3p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p9);

		JButton btn3p10 = new JButton("");
		btn3p10.setBounds(235, 170, 25, 25);
		btn3p10.setName("btn3p10");
		btn3p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p10);

		JButton btn3p11 = new JButton("");
		btn3p11.setBounds(260, 170, 25, 25);
		btn3p11.setName("btn3p11");
		btn3p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p11);

		JButton btn3p12 = new JButton("");
		btn3p12.setBounds(285, 170, 25, 25);
		btn3p12.setName("btn3p12");
		btn3p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p12);

		JButton btn3p13 = new JButton("");
		btn3p13.setBounds(310, 170, 25, 25);
		btn3p13.setName("btn3p13");
		btn3p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p13);

		JButton btn3p14 = new JButton("");
		btn3p14.setBounds(335, 170, 25, 25);
		btn3p14.setName("btn3p14");
		btn3p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p14);

		JButton btn3p15 = new JButton("");
		btn3p15.setBounds(360, 170, 25, 25);
		btn3p15.setName("btn3p15");
		btn3p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn3p15);

		JButton btn4p1 = new JButton("");
		btn4p1.setBounds(10, 195, 25, 25);
		btn4p1.setName("btn4p1");
		btn4p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p1);

		JButton btn4p2 = new JButton("");
		btn4p2.setBounds(35, 195, 25, 25);
		btn4p2.setName("btn4p2");
		btn4p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p2);

		JButton btn4p3 = new JButton("");
		btn4p3.setBounds(60, 195, 25, 25);
		btn4p3.setName("btn4p3");
		btn4p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p3);

		JButton btn4p4 = new JButton("");
		btn4p4.setBounds(85, 195, 25, 25);
		btn4p4.setName("btn4p4");
		btn4p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p4);

		JButton btn4p5 = new JButton("");
		btn4p5.setBounds(110, 195, 25, 25);
		btn4p5.setName("btn4p5");
		btn4p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p5);

		JButton btn4p6 = new JButton("");
		btn4p6.setBounds(135, 195, 25, 25);
		btn4p6.setName("btn4p6");
		btn4p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p6);

		JButton btn4p7 = new JButton("");
		btn4p7.setBounds(160, 195, 25, 25);
		btn4p7.setName("btn4p7");
		btn4p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p7);

		JButton btn4p8 = new JButton("");
		btn4p8.setBounds(185, 195, 25, 25);
		btn4p8.setName("btn4p8");
		btn4p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p8);

		JButton btn4p9 = new JButton("");
		btn4p9.setBounds(210, 195, 25, 25);
		btn4p9.setName("btn4p9");
		btn4p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p9);

		JButton btn4p10 = new JButton("");
		btn4p10.setBounds(235, 195, 25, 25);
		btn4p10.setName("btn4p10");
		btn4p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p10);

		JButton btn4p11 = new JButton("");
		btn4p11.setBounds(260, 195, 25, 25);
		btn4p11.setName("btn4p11");
		btn4p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p11);

		JButton btn4p12 = new JButton("");
		btn4p12.setBounds(285, 195, 25, 25);
		btn4p12.setName("btn4p12");
		btn4p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p12);

		JButton btn4p13 = new JButton("");
		btn4p13.setBounds(310, 195, 25, 25);
		btn4p13.setName("btn4p13");
		btn4p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p13);

		JButton btn4p14 = new JButton("");
		btn4p14.setBounds(335, 195, 25, 25);
		btn4p14.setName("btn4p14");
		btn4p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p14);

		JButton btn4p15 = new JButton("");
		btn4p15.setBounds(360, 195, 25, 25);
		btn4p15.setName("btn4p15");
		btn4p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn4p15);

		JButton btn5p1 = new JButton("");
		btn5p1.setBounds(10, 220, 25, 25);
		btn5p1.setName("btn5p1");
		btn5p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p1);

		JButton btn5p2 = new JButton("");
		btn5p2.setBounds(35, 220, 25, 25);
		btn5p2.setName("btn5p2");
		btn5p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p2);

		JButton btn5p3 = new JButton("");
		btn5p3.setBounds(60, 220, 25, 25);
		btn5p3.setName("btn5p3");
		btn5p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p3);

		JButton btn5p4 = new JButton("");
		btn5p4.setBounds(85, 220, 25, 25);
		btn5p4.setName("btn5p4");
		btn5p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p4);

		JButton btn5p5 = new JButton("");
		btn5p5.setBounds(110, 220, 25, 25);
		btn5p5.setName("btn5p5");
		btn5p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p5);

		JButton btn5p6 = new JButton("");
		btn5p6.setBounds(135, 220, 25, 25);
		btn5p6.setName("btn5p6");
		btn5p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p6);

		JButton btn5p7 = new JButton("");
		btn5p7.setBounds(160, 220, 25, 25);
		btn5p7.setName("btn5p7");
		btn5p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p7);

		JButton btn5p8 = new JButton("");
		btn5p8.setBounds(185, 220, 25, 25);
		btn5p8.setName("btn5p8");
		btn5p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p8);

		JButton btn5p9 = new JButton("");
		btn5p9.setBounds(210, 220, 25, 25);
		btn5p9.setName("btn5p9");
		btn5p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p9);

		JButton btn5p10 = new JButton("");
		btn5p10.setBounds(235, 220, 25, 25);
		btn5p10.setName("btn5p10");
		btn5p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p10);

		JButton btn5p11 = new JButton("");
		btn5p11.setBounds(260, 220, 25, 25);
		btn5p11.setName("btn5p11");
		btn5p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p11);

		JButton btn5p12 = new JButton("");
		btn5p12.setBounds(285, 220, 25, 25);
		btn5p12.setName("btn5p12");
		btn5p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p12);

		JButton btn5p13 = new JButton("");
		btn5p13.setBounds(310, 220, 25, 25);
		btn5p13.setName("btn5p13");
		btn5p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p13);

		JButton btn5p14 = new JButton("");
		btn5p14.setBounds(335, 220, 25, 25);
		btn5p14.setName("btn5p14");
		btn5p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p14);

		JButton btn5p15 = new JButton("");
		btn5p15.setBounds(360, 220, 25, 25);
		btn5p15.setName("btn5p15");
		btn5p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn5p15);

		JButton btn6p1 = new JButton("");
		btn6p1.setBounds(10, 245, 25, 25);
		btn6p1.setName("btn6p1");
		btn6p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p1);

		JButton btn6p2 = new JButton("");
		btn6p2.setBounds(35, 245, 25, 25);
		btn6p2.setName("btn6p2");
		btn6p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p2);

		JButton btn6p3 = new JButton("");
		btn6p3.setBounds(60, 245, 25, 25);
		btn6p3.setName("btn6p3");
		btn6p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p3);

		JButton btn6p4 = new JButton("");
		btn6p4.setBounds(85, 245, 25, 25);
		btn6p4.setName("btn6p4");
		btn6p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p4);

		JButton btn6p5 = new JButton("");
		btn6p5.setBounds(110, 245, 25, 25);
		btn6p5.setName("btn6p5");
		btn6p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p5);

		JButton btn6p6 = new JButton("");
		btn6p6.setBounds(135, 245, 25, 25);
		btn6p6.setName("btn6p6");
		btn6p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p6);

		JButton btn6p7 = new JButton("");
		btn6p7.setBounds(160, 245, 25, 25);
		btn6p7.setName("btn6p7");
		btn6p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p7);

		JButton btn6p8 = new JButton("");
		btn6p8.setBounds(185, 245, 25, 25);
		btn6p8.setName("btn6p8");
		btn6p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p8);

		JButton btn6p9 = new JButton("");
		btn6p9.setBounds(210, 245, 25, 25);
		btn6p9.setName("btn6p9");
		btn6p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p9);

		JButton btn6p10 = new JButton("");
		btn6p10.setBounds(235, 245, 25, 25);
		btn6p10.setName("btn6p10");
		btn6p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p10);

		JButton btn6p11 = new JButton("");
		btn6p11.setBounds(260, 245, 25, 25);
		btn6p11.setName("btn6p11");
		btn6p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p11);

		JButton btn6p12 = new JButton("");
		btn6p12.setBounds(285, 245, 25, 25);
		btn6p12.setName("btn6p12");
		btn6p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p12);

		JButton btn6p13 = new JButton("");
		btn6p13.setBounds(310, 245, 25, 25);
		btn6p13.setName("btn6p13");
		btn6p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p13);

		JButton btn6p14 = new JButton("");
		btn6p14.setBounds(335, 245, 25, 25);
		btn6p14.setName("btn6p14");
		btn6p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p14);

		JButton btn6p15 = new JButton("");
		btn6p15.setBounds(360, 245, 25, 25);
		btn6p15.setName("btn6p15");
		btn6p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn6p15);

		JButton btn7p1 = new JButton("");
		btn7p1.setBounds(10, 270, 25, 25);
		btn7p1.setName("btn7p1");
		btn7p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p1);

		JButton btn7p2 = new JButton("");
		btn7p2.setBounds(35, 270, 25, 25);
		btn7p2.setName("btn7p2");
		btn7p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p2);

		JButton btn7p3 = new JButton("");
		btn7p3.setBounds(60, 270, 25, 25);
		btn7p3.setName("btn7p3");
		btn7p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p3);

		JButton btn7p4 = new JButton("");
		btn7p4.setBounds(85, 270, 25, 25);
		btn7p4.setName("btn7p4");
		btn7p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p4);

		JButton btn7p5 = new JButton("");
		btn7p5.setBounds(110, 270, 25, 25);
		btn7p5.setName("btn7p5");
		btn7p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p5);

		JButton btn7p6 = new JButton("");
		btn7p6.setBounds(135, 270, 25, 25);
		btn7p6.setName("btn7p6");
		btn7p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p6);

		JButton btn7p7 = new JButton("");
		btn7p7.setBounds(160, 270, 25, 25);
		btn7p7.setName("btn7p7");
		btn7p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p7);

		JButton btn7p8 = new JButton("");
		btn7p8.setBounds(185, 270, 25, 25);
		btn7p8.setName("btn7p8");
		btn7p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p8);

		JButton btn7p9 = new JButton("");
		btn7p9.setBounds(210, 270, 25, 25);
		btn7p9.setName("btn7p9");
		btn7p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p9);

		JButton btn7p10 = new JButton("");
		btn7p10.setBounds(235, 270, 25, 25);
		btn7p10.setName("btn7p10");
		btn7p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p10);

		JButton btn7p11 = new JButton("");
		btn7p11.setBounds(260, 270, 25, 25);
		btn7p11.setName("btn7p11");
		btn7p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p11);

		JButton btn7p12 = new JButton("");
		btn7p12.setBounds(285, 270, 25, 25);
		btn7p12.setName("btn7p12");
		btn7p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p12);

		JButton btn7p13 = new JButton("");
		btn7p13.setBounds(310, 270, 25, 25);
		btn7p13.setName("btn7p13");
		btn7p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p13);

		JButton btn7p14 = new JButton("");
		btn7p14.setBounds(335, 270, 25, 25);
		btn7p14.setName("btn7p14");
		btn7p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p14);

		JButton btn7p15 = new JButton("");
		btn7p15.setBounds(360, 270, 25, 25);
		btn7p15.setName("btn7p15");
		btn7p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn7p15);

		JButton btn8p1 = new JButton("");
		btn8p1.setBounds(10, 295, 25, 25);
		btn8p1.setName("btn8p1");
		btn8p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p1);

		JButton btn8p2 = new JButton("");
		btn8p2.setBounds(35, 295, 25, 25);
		btn8p2.setName("btn8p2");
		btn8p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p2);

		JButton btn8p3 = new JButton("");
		btn8p3.setBounds(60, 295, 25, 25);
		btn8p3.setName("btn8p3");
		btn8p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p3);

		JButton btn8p4 = new JButton("");
		btn8p4.setBounds(85, 295, 25, 25);
		btn8p4.setName("btn8p4");
		btn8p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p4);

		JButton btn8p5 = new JButton("");
		btn8p5.setBounds(110, 295, 25, 25);
		btn8p5.setName("btn8p5");
		btn8p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p5);

		JButton btn8p6 = new JButton("");
		btn8p6.setBounds(135, 295, 25, 25);
		btn8p6.setName("btn8p6");
		btn8p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p6);

		JButton btn8p7 = new JButton("");
		btn8p7.setBounds(160, 295, 25, 25);
		btn8p7.setName("btn8p7");
		btn8p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p7);

		JButton btn8p8 = new JButton("");
		btn8p8.setBounds(185, 295, 25, 25);
		btn8p8.setName("btn8p8");
		btn8p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p8);

		JButton btn8p9 = new JButton("");
		btn8p9.setBounds(210, 295, 25, 25);
		btn8p9.setName("btn8p9");
		btn8p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p9);

		JButton btn8p10 = new JButton("");
		btn8p10.setBounds(235, 295, 25, 25);
		btn8p10.setName("btn8p10");
		btn8p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p10);

		JButton btn8p11 = new JButton("");
		btn8p11.setBounds(260, 295, 25, 25);
		btn8p11.setName("btn8p11");
		btn8p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p11);

		JButton btn8p12 = new JButton("");
		btn8p12.setBounds(285, 295, 25, 25);
		btn8p12.setName("btn8p12");
		btn8p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p12);

		JButton btn8p13 = new JButton("");
		btn8p13.setBounds(310, 295, 25, 25);
		btn8p13.setName("btn8p13");
		btn8p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p13);

		JButton btn8p14 = new JButton("");
		btn8p14.setBounds(335, 295, 25, 25);
		btn8p14.setName("btn8p14");
		btn8p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p14);

		JButton btn8p15 = new JButton("");
		btn8p15.setBounds(360, 295, 25, 25);
		btn8p15.setName("btn8p15");
		btn8p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn8p15);

		JButton btn9p1 = new JButton("");
		btn9p1.setBounds(10, 320, 25, 25);
		btn9p1.setName("btn9p1");
		btn9p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p1);

		JButton btn9p2 = new JButton("");
		btn9p2.setBounds(35, 320, 25, 25);
		btn9p2.setName("btn9p2");
		btn9p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p2);

		JButton btn9p3 = new JButton("");
		btn9p3.setBounds(60, 320, 25, 25);
		btn9p3.setName("btn9p3");
		btn9p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p3);

		JButton btn9p4 = new JButton("");
		btn9p4.setBounds(85, 320, 25, 25);
		btn9p4.setName("btn9p4");
		btn9p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p4);

		JButton btn9p5 = new JButton("");
		btn9p5.setBounds(110, 320, 25, 25);
		btn9p5.setName("btn9p5");
		btn9p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p5);

		JButton btn9p6 = new JButton("");
		btn9p6.setBounds(135, 320, 25, 25);
		btn9p6.setName("btn9p6");
		btn9p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p6);

		JButton btn9p7 = new JButton("");
		btn9p7.setBounds(160, 320, 25, 25);
		btn9p7.setName("btn9p7");
		btn9p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p7);

		JButton btn9p8 = new JButton("");
		btn9p8.setBounds(185, 320, 25, 25);
		btn9p8.setName("btn9p8");
		btn9p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p8);

		JButton btn9p9 = new JButton("");
		btn9p9.setBounds(210, 320, 25, 25);
		btn9p9.setName("btn9p9");
		btn9p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p9);

		JButton btn9p10 = new JButton("");
		btn9p10.setBounds(235, 320, 25, 25);
		btn9p10.setName("btn9p10");
		btn9p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p10);

		JButton btn9p11 = new JButton("");
		btn9p11.setBounds(260, 320, 25, 25);
		btn9p11.setName("btn9p11");
		btn9p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p11);

		JButton btn9p12 = new JButton("");
		btn9p12.setBounds(285, 320, 25, 25);
		btn9p12.setName("btn9p12");
		btn9p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p12);

		JButton btn9p13 = new JButton("");
		btn9p13.setBounds(310, 320, 25, 25);
		btn9p13.setName("btn9p13");
		btn9p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p13);

		JButton btn9p14 = new JButton("");
		btn9p14.setBounds(335, 320, 25, 25);
		btn9p14.setName("btn9p14");
		btn9p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p14);

		JButton btn9p15 = new JButton("");
		btn9p15.setBounds(360, 320, 25, 25);
		btn9p15.setName("btn9p15");
		btn9p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn9p15);

		JButton btn10p1 = new JButton("");
		btn10p1.setBounds(10, 345, 25, 25);
		btn10p1.setName("btn10p1");
		btn10p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p1);

		JButton btn10p2 = new JButton("");
		btn10p2.setBounds(35, 345, 25, 25);
		btn10p2.setName("btn10p2");
		btn10p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p2);

		JButton btn10p3 = new JButton("");
		btn10p3.setBounds(60, 345, 25, 25);
		btn10p3.setName("btn10p3");
		btn10p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p3);

		JButton btn10p4 = new JButton("");
		btn10p4.setBounds(85, 345, 25, 25);
		btn10p4.setName("btn10p4");
		btn10p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p4);

		JButton btn10p5 = new JButton("");
		btn10p5.setBounds(110, 345, 25, 25);
		btn10p5.setName("btn10p5");
		btn10p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p5);

		JButton btn10p6 = new JButton("");
		btn10p6.setBounds(135, 345, 25, 25);
		btn10p6.setName("btn10p6");
		btn10p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p6);

		JButton btn10p7 = new JButton("");
		btn10p7.setBounds(160, 345, 25, 25);
		btn10p7.setName("btn10p7");
		btn10p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p7);

		JButton btn10p8 = new JButton("");
		btn10p8.setBounds(185, 345, 25, 25);
		btn10p8.setName("btn10p8");
		btn10p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p8);

		JButton btn10p9 = new JButton("");
		btn10p9.setBounds(210, 345, 25, 25);
		btn10p9.setName("btn10p9");
		btn10p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p9);

		JButton btn10p10 = new JButton("");
		btn10p10.setBounds(235, 345, 25, 25);
		btn10p10.setName("btn10p10");
		btn10p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p10);

		JButton btn10p11 = new JButton("");
		btn10p11.setBounds(260, 345, 25, 25);
		btn10p11.setName("btn10p11");
		btn10p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p11);

		JButton btn10p12 = new JButton("");
		btn10p12.setBounds(285, 345, 25, 25);
		btn10p12.setName("btn10p12");
		btn10p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p12);

		JButton btn10p13 = new JButton("");
		btn10p13.setBounds(310, 345, 25, 25);
		btn10p13.setName("btn10p13");
		btn10p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p13);

		JButton btn10p14 = new JButton("");
		btn10p14.setBounds(335, 345, 25, 25);
		btn10p14.setName("btn10p14");
		btn10p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p14);

		JButton btn10p15 = new JButton("");
		btn10p15.setBounds(360, 345, 25, 25);
		btn10p15.setName("btn10p15");
		btn10p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn10p15);

		JButton btn11p1 = new JButton("");
		btn11p1.setBounds(10, 370, 25, 25);
		btn11p1.setName("btn11p1");
		btn11p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p1);

		JButton btn11p2 = new JButton("");
		btn11p2.setBounds(35, 370, 25, 25);
		btn11p2.setName("btn11p2");
		btn11p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p2);

		JButton btn11p3 = new JButton("");
		btn11p3.setBounds(60, 370, 25, 25);
		btn11p3.setName("btn11p3");
		btn11p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p3);

		JButton btn11p4 = new JButton("");
		btn11p4.setBounds(85, 370, 25, 25);
		btn11p4.setName("btn11p4");
		btn11p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p4);

		JButton btn11p5 = new JButton("");
		btn11p5.setBounds(110, 370, 25, 25);
		btn11p5.setName("btn11p5");
		btn11p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p5);

		JButton btn11p6 = new JButton("");
		btn11p6.setBounds(135, 370, 25, 25);
		btn11p6.setName("btn11p6");
		btn11p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p6);

		JButton btn11p7 = new JButton("");
		btn11p7.setBounds(160, 370, 25, 25);
		btn11p7.setName("btn11p7");
		btn11p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p7);

		JButton btn11p8 = new JButton("");
		btn11p8.setBounds(185, 370, 25, 25);
		btn11p8.setName("btn11p8");
		btn11p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p8);

		JButton btn11p9 = new JButton("");
		btn11p9.setBounds(210, 370, 25, 25);
		btn11p9.setName("btn11p9");
		btn11p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p9);

		JButton btn11p10 = new JButton("");
		btn11p10.setBounds(235, 370, 25, 25);
		btn11p10.setName("btn11p10");
		btn11p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p10);

		JButton btn11p11 = new JButton("");
		btn11p11.setBounds(260, 370, 25, 25);
		btn11p11.setName("btn11p11");
		btn11p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p11);

		JButton btn11p12 = new JButton("");
		btn11p12.setBounds(285, 370, 25, 25);
		btn11p12.setName("btn11p12");
		btn11p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p12);

		JButton btn11p13 = new JButton("");
		btn11p13.setBounds(310, 370, 25, 25);
		btn11p13.setName("btn11p13");
		btn11p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p13);

		JButton btn11p14 = new JButton("");
		btn11p14.setBounds(335, 370, 25, 25);
		btn11p14.setName("btn11p14");
		btn11p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p14);

		JButton btn11p15 = new JButton("");
		btn11p15.setBounds(360, 370, 25, 25);
		btn11p15.setName("btn11p15");
		btn11p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn11p15);

		JButton btn12p1 = new JButton("");
		btn12p1.setBounds(10, 395, 25, 25);
		btn12p1.setName("btn12p1");
		btn12p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p1);

		JButton btn12p2 = new JButton("");
		btn12p2.setBounds(35, 395, 25, 25);
		btn12p2.setName("btn12p2");
		btn12p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p2);

		JButton btn12p3 = new JButton("");
		btn12p3.setBounds(60, 395, 25, 25);
		btn12p3.setName("btn12p3");
		btn12p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p3);

		JButton btn12p4 = new JButton("");
		btn12p4.setBounds(85, 395, 25, 25);
		btn12p4.setName("btn12p4");
		btn12p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p4);

		JButton btn12p5 = new JButton("");
		btn12p5.setBounds(110, 395, 25, 25);
		btn12p5.setName("btn12p5");
		btn12p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p5);

		JButton btn12p6 = new JButton("");
		btn12p6.setBounds(135, 395, 25, 25);
		btn12p6.setName("btn12p6");
		btn12p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p6);

		JButton btn12p7 = new JButton("");
		btn12p7.setBounds(160, 395, 25, 25);
		btn12p7.setName("btn12p7");
		btn12p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p7);

		JButton btn12p8 = new JButton("");
		btn12p8.setBounds(185, 395, 25, 25);
		btn12p8.setName("btn12p8");
		btn12p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p8);

		JButton btn12p9 = new JButton("");
		btn12p9.setBounds(210, 395, 25, 25);
		btn12p9.setName("btn12p9");
		btn12p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p9);

		JButton btn12p10 = new JButton("");
		btn12p10.setBounds(235, 395, 25, 25);
		btn12p10.setName("btn12p10");
		btn12p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p10);

		JButton btn12p11 = new JButton("");
		btn12p11.setBounds(260, 395, 25, 25);
		btn12p11.setName("btn12p11");
		btn12p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p11);

		JButton btn12p12 = new JButton("");
		btn12p12.setBounds(285, 395, 25, 25);
		btn12p12.setName("btn12p12");
		btn12p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p12);

		JButton btn12p13 = new JButton("");
		btn12p13.setBounds(310, 395, 25, 25);
		btn12p13.setName("btn12p13");
		btn12p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p13);

		JButton btn12p14 = new JButton("");
		btn12p14.setBounds(335, 395, 25, 25);
		btn12p14.setName("btn12p14");
		btn12p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p14);

		JButton btn12p15 = new JButton("");
		btn12p15.setBounds(360, 395, 25, 25);
		btn12p15.setName("btn12p15");
		btn12p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn12p15);

		JButton btn13p1 = new JButton("");
		btn13p1.setBounds(10, 420, 25, 25);
		btn13p1.setName("btn13p1");
		btn13p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p1);

		JButton btn13p2 = new JButton("");
		btn13p2.setBounds(35, 420, 25, 25);
		btn13p2.setName("btn13p2");
		btn13p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p2);

		JButton btn13p3 = new JButton("");
		btn13p3.setBounds(60, 420, 25, 25);
		btn13p3.setName("btn13p3");
		btn13p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p3);

		JButton btn13p4 = new JButton("");
		btn13p4.setBounds(85, 420, 25, 25);
		btn13p4.setName("btn13p4");
		btn13p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p4);

		JButton btn13p5 = new JButton("");
		btn13p5.setBounds(110, 420, 25, 25);
		btn13p5.setName("btn13p5");
		btn13p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p5);

		JButton btn13p6 = new JButton("");
		btn13p6.setBounds(135, 420, 25, 25);
		btn13p6.setName("btn13p6");
		btn13p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p6);

		JButton btn13p7 = new JButton("");
		btn13p7.setBounds(160, 420, 25, 25);
		btn13p7.setName("btn13p7");
		btn13p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p7);

		JButton btn13p8 = new JButton("");
		btn13p8.setBounds(185, 420, 25, 25);
		btn13p8.setName("btn13p8");
		btn13p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p8);

		JButton btn13p9 = new JButton("");
		btn13p9.setBounds(210, 420, 25, 25);
		btn13p9.setName("btn13p9");
		btn13p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p9);

		JButton btn13p10 = new JButton("");
		btn13p10.setBounds(235, 420, 25, 25);
		btn13p10.setName("btn13p10");
		btn13p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p10);

		JButton btn13p11 = new JButton("");
		btn13p11.setBounds(260, 420, 25, 25);
		btn13p11.setName("btn13p11");
		btn13p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p11);

		JButton btn13p12 = new JButton("");
		btn13p12.setBounds(285, 420, 25, 25);
		btn13p12.setName("btn13p12");
		btn13p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p12);

		JButton btn13p13 = new JButton("");
		btn13p13.setBounds(310, 420, 25, 25);
		btn13p13.setName("btn13p13");
		btn13p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p13);

		JButton btn13p14 = new JButton("");
		btn13p14.setBounds(335, 420, 25, 25);
		btn13p14.setName("btn13p14");
		btn13p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p14);

		JButton btn13p15 = new JButton("");
		btn13p15.setBounds(360, 420, 25, 25);
		btn13p15.setName("btn13p15");
		btn13p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn13p15);

		JButton btn14p1 = new JButton("");
		btn14p1.setBounds(10, 445, 25, 25);
		btn14p1.setName("btn14p1");
		btn14p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p1);

		JButton btn14p2 = new JButton("");
		btn14p2.setBounds(35, 445, 25, 25);
		btn14p2.setName("btn14p2");
		btn14p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p2);

		JButton btn14p3 = new JButton("");
		btn14p3.setBounds(60, 445, 25, 25);
		btn14p3.setName("btn14p3");
		btn14p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p3);

		JButton btn14p4 = new JButton("");
		btn14p4.setBounds(85, 445, 25, 25);
		btn14p4.setName("btn14p4");
		btn14p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p4);

		JButton btn14p5 = new JButton("");
		btn14p5.setBounds(110, 445, 25, 25);
		btn14p5.setName("btn14p5");
		btn14p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p5);

		JButton btn14p6 = new JButton("");
		btn14p6.setBounds(135, 445, 25, 25);
		btn14p6.setName("btn14p6");
		btn14p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p6);

		JButton btn14p7 = new JButton("");
		btn14p7.setBounds(160, 445, 25, 25);
		btn14p7.setName("btn14p7");
		btn14p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p7);

		JButton btn14p8 = new JButton("");
		btn14p8.setBounds(185, 445, 25, 25);
		btn14p8.setName("btn14p8");
		btn14p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p8);

		JButton btn14p9 = new JButton("");
		btn14p9.setBounds(210, 445, 25, 25);
		btn14p9.setName("btn14p9");
		btn14p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p9);

		JButton btn14p10 = new JButton("");
		btn14p10.setBounds(235, 445, 25, 25);
		btn14p10.setName("btn14p10");
		btn14p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p10);

		JButton btn14p11 = new JButton("");
		btn14p11.setBounds(260, 445, 25, 25);
		btn14p11.setName("btn14p11");
		btn14p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p11);

		JButton btn14p12 = new JButton("");
		btn14p12.setBounds(285, 445, 25, 25);
		btn14p12.setName("btn14p12");
		btn14p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p12);

		JButton btn14p13 = new JButton("");
		btn14p13.setBounds(310, 445, 25, 25);
		btn14p13.setName("btn14p13");
		btn14p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p13);

		JButton btn14p14 = new JButton("");
		btn14p14.setBounds(335, 445, 25, 25);
		btn14p14.setName("btn14p14");
		btn14p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p14);

		JButton btn14p15 = new JButton("");
		btn14p15.setBounds(360, 445, 25, 25);
		btn14p15.setName("btn14p15");
		btn14p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn14p15);

		JButton btn15p1 = new JButton("");
		btn15p1.setBounds(10, 470, 25, 25);
		btn15p1.setName("btn15p1");
		btn15p1.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p1);

		JButton btn15p2 = new JButton("");
		btn15p2.setBounds(35, 470, 25, 25);
		btn15p2.setName("btn15p2");
		btn15p2.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p2);

		JButton btn15p3 = new JButton("");
		btn15p3.setBounds(60, 470, 25, 25);
		btn15p3.setName("btn15p3");
		btn15p3.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p3);

		JButton btn15p4 = new JButton("");
		btn15p4.setBounds(85, 470, 25, 25);
		btn15p4.setName("btn15p4");
		btn15p4.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p4);

		JButton btn15p5 = new JButton("");
		btn15p5.setBounds(110, 470, 25, 25);
		btn15p5.setName("btn15p5");
		btn15p5.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p5);

		JButton btn15p6 = new JButton("");
		btn15p6.setBounds(135, 470, 25, 25);
		btn15p6.setName("btn15p6");
		btn15p6.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p6);

		JButton btn15p7 = new JButton("");
		btn15p7.setBounds(160, 470, 25, 25);
		btn15p7.setName("btn15p7");
		btn15p7.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p7);

		JButton btn15p8 = new JButton("");
		btn15p8.setBounds(185, 470, 25, 25);
		btn15p8.setName("btn15p8");
		btn15p8.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p8);

		JButton btn15p9 = new JButton("");
		btn15p9.setBounds(210, 470, 25, 25);
		btn15p9.setName("btn15p9");
		btn15p9.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p9);

		JButton btn15p10 = new JButton("");
		btn15p10.setBounds(235, 470, 25, 25);
		btn15p10.setName("btn15p10");
		btn15p10.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p10);

		JButton btn15p11 = new JButton("");
		btn15p11.setBounds(260, 470, 25, 25);
		btn15p11.setName("btn15p11");
		btn15p11.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p11);

		JButton btn15p12 = new JButton("");
		btn15p12.setBounds(285, 470, 25, 25);
		btn15p12.setName("btn15p12");
		btn15p12.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p12);

		JButton btn15p13 = new JButton("");
		btn15p13.setBounds(310, 470, 25, 25);
		btn15p13.setName("btn15p13");
		btn15p13.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p13);

		JButton btn15p14 = new JButton("");
		btn15p14.setBounds(335, 470, 25, 25);
		btn15p14.setName("btn15p14");
		btn15p14.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p14);

		JButton btn15p15 = new JButton("");
		btn15p15.setBounds(360, 470, 25, 25);
		btn15p15.setName("btn15p15");
		btn15p15.addActionListener(playerGridListener);
		panelPlayerGrid.add(btn15p15);

		JButton btn1e1 = new JButton("");
		btn1e1.setBounds(407, 120, 25, 25);
		btn1e1.setName("btn1e1");
		btn1e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e1);

		JButton btn1e2 = new JButton("");
		btn1e2.setBounds(432, 120, 25, 25);
		btn1e2.setName("btn1e2");
		btn1e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e2);

		JButton btn1e3 = new JButton("");
		btn1e3.setBounds(457, 120, 25, 25);
		btn1e3.setName("btn1e3");
		btn1e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e3);

		JButton btn1e4 = new JButton("");
		btn1e4.setBounds(482, 120, 25, 25);
		btn1e4.setName("btn1e4");
		btn1e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e4);

		JButton btn1e5 = new JButton("");
		btn1e5.setBounds(507, 120, 25, 25);
		btn1e5.setName("btn1e5");
		btn1e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e5);

		JButton btn1e6 = new JButton("");
		btn1e6.setBounds(532, 120, 25, 25);
		btn1e6.setName("btn1e6");
		btn1e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e6);

		JButton btn1e7 = new JButton("");
		btn1e7.setBounds(557, 120, 25, 25);
		btn1e7.setName("btn1e7");
		btn1e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e7);

		JButton btn1e8 = new JButton("");
		btn1e8.setBounds(582, 120, 25, 25);
		btn1e8.setName("btn1e8");
		btn1e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e8);

		JButton btn1e9 = new JButton("");
		btn1e9.setBounds(607, 120, 25, 25);
		btn1e9.setName("btn1e9");
		btn1e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e9);

		JButton btn1e10 = new JButton("");
		btn1e10.setBounds(632, 120, 25, 25);
		btn1e10.setName("btn1e10");
		btn1e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e10);

		JButton btn1e11 = new JButton("");
		btn1e11.setBounds(657, 120, 25, 25);
		btn1e11.setName("btn1e11");
		btn1e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e11);

		JButton btn1e12 = new JButton("");
		btn1e12.setBounds(682, 120, 25, 25);
		btn1e12.setName("btn1e12");
		btn1e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e12);

		JButton btn1e13 = new JButton("");
		btn1e13.setBounds(707, 120, 25, 25);
		btn1e13.setName("btn1e13");
		btn1e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e13);

		JButton btn1e14 = new JButton("");
		btn1e14.setBounds(732, 120, 25, 25);
		btn1e14.setName("btn1e14");
		btn1e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e14);

		JButton btn1e15 = new JButton("");
		btn1e15.setBounds(757, 120, 25, 25);
		btn1e15.setName("btn1e15");
		btn1e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn1e15);

		JButton btn2e1 = new JButton("");
		btn2e1.setBounds(407, 145, 25, 25);
		btn2e1.setName("btn2e1");
		btn2e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e1);

		JButton btn2e2 = new JButton("");
		btn2e2.setBounds(432, 145, 25, 25);
		btn2e2.setName("btn2e2");
		btn2e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e2);

		JButton btn2e3 = new JButton("");
		btn2e3.setBounds(457, 145, 25, 25);
		btn2e3.setName("btn2e3");
		btn2e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e3);

		JButton btn2e4 = new JButton("");
		btn2e4.setBounds(482, 145, 25, 25);
		btn2e4.setName("btn2e4");
		btn2e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e4);

		JButton btn2e5 = new JButton("");
		btn2e5.setBounds(507, 145, 25, 25);
		btn2e5.setName("btn2e5");
		btn2e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e5);

		JButton btn2e6 = new JButton("");
		btn2e6.setBounds(532, 145, 25, 25);
		btn2e6.setName("btn2e6");
		btn2e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e6);

		JButton btn2e7 = new JButton("");
		btn2e7.setBounds(557, 145, 25, 25);
		btn2e7.setName("btn2e7");
		btn2e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e7);

		JButton btn2e8 = new JButton("");
		btn2e8.setBounds(582, 145, 25, 25);
		btn2e8.setName("btn2e8");
		btn2e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e8);

		JButton btn2e9 = new JButton("");
		btn2e9.setBounds(607, 145, 25, 25);
		btn2e9.setName("btn2e9");
		btn2e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e9);

		JButton btn2e10 = new JButton("");
		btn2e10.setBounds(632, 145, 25, 25);
		btn2e10.setName("btn2e10");
		btn2e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e10);

		JButton btn2e11 = new JButton("");
		btn2e11.setBounds(657, 145, 25, 25);
		btn2e11.setName("btn2e11");
		btn2e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e11);

		JButton btn2e12 = new JButton("");
		btn2e12.setBounds(682, 145, 25, 25);
		btn2e12.setName("btn2e12");
		btn2e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e12);

		JButton btn2e13 = new JButton("");
		btn2e13.setBounds(707, 145, 25, 25);
		btn2e13.setName("btn2e13");
		btn2e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e13);

		JButton btn2e14 = new JButton("");
		btn2e14.setBounds(732, 145, 25, 25);
		btn2e14.setName("btn2e14");
		btn2e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e14);

		JButton btn2e15 = new JButton("");
		btn2e15.setBounds(757, 145, 25, 25);
		btn2e15.setName("btn2e15");
		btn2e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn2e15);

		JButton btn3e1 = new JButton("");
		btn3e1.setBounds(407, 170, 25, 25);
		btn3e1.setName("btn3e1");
		btn3e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e1);

		JButton btn3e2 = new JButton("");
		btn3e2.setBounds(432, 170, 25, 25);
		btn3e2.setName("btn3e2");
		btn3e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e2);

		JButton btn3e3 = new JButton("");
		btn3e3.setBounds(457, 170, 25, 25);
		btn3e3.setName("btn3e3");
		btn3e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e3);

		JButton btn3e4 = new JButton("");
		btn3e4.setBounds(482, 170, 25, 25);
		btn3e4.setName("btn3e4");
		btn3e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e4);

		JButton btn3e5 = new JButton("");
		btn3e5.setBounds(507, 170, 25, 25);
		btn3e5.setName("btn3e5");
		btn3e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e5);

		JButton btn3e6 = new JButton("");
		btn3e6.setBounds(532, 170, 25, 25);
		btn3e6.setName("btn3e6");
		btn3e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e6);

		JButton btn3e7 = new JButton("");
		btn3e7.setBounds(557, 170, 25, 25);
		btn3e7.setName("btn3e7");
		btn3e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e7);

		JButton btn3e8 = new JButton("");
		btn3e8.setBounds(582, 170, 25, 25);
		btn3e8.setName("btn3e8");
		btn3e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e8);

		JButton btn3e9 = new JButton("");
		btn3e9.setBounds(607, 170, 25, 25);
		btn3e9.setName("btn3e9");
		btn3e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e9);

		JButton btn3e10 = new JButton("");
		btn3e10.setBounds(632, 170, 25, 25);
		btn3e10.setName("btn3e10");
		btn3e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e10);

		JButton btn3e11 = new JButton("");
		btn3e11.setBounds(657, 170, 25, 25);
		btn3e11.setName("btn3e11");
		btn3e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e11);

		JButton btn3e12 = new JButton("");
		btn3e12.setBounds(682, 170, 25, 25);
		btn3e12.setName("btn3e12");
		btn3e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e12);

		JButton btn3e13 = new JButton("");
		btn3e13.setBounds(707, 170, 25, 25);
		btn3e13.setName("btn3e13");
		btn3e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e13);

		JButton btn3e14 = new JButton("");
		btn3e14.setBounds(732, 170, 25, 25);
		btn3e14.setName("btn3e14");
		btn3e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e14);

		JButton btn3e15 = new JButton("");
		btn3e15.setBounds(757, 170, 25, 25);
		btn3e15.setName("btn3e15");
		btn3e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn3e15);

		JButton btn4e1 = new JButton("");
		btn4e1.setBounds(407, 195, 25, 25);
		btn4e1.setName("btn4e1");
		btn4e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e1);

		JButton btn4e2 = new JButton("");
		btn4e2.setBounds(432, 195, 25, 25);
		btn4e2.setName("btn4e2");
		btn4e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e2);

		JButton btn4e3 = new JButton("");
		btn4e3.setBounds(457, 195, 25, 25);
		btn4e3.setName("btn4e3");
		btn4e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e3);

		JButton btn4e4 = new JButton("");
		btn4e4.setBounds(482, 195, 25, 25);
		btn4e4.setName("btn4e4");
		btn4e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e4);

		JButton btn4e5 = new JButton("");
		btn4e5.setBounds(507, 195, 25, 25);
		btn4e5.setName("btn4e5");
		btn4e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e5);

		JButton btn4e6 = new JButton("");
		btn4e6.setBounds(532, 195, 25, 25);
		btn4e6.setName("btn4e6");
		btn4e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e6);

		JButton btn4e7 = new JButton("");
		btn4e7.setBounds(557, 195, 25, 25);
		btn4e7.setName("btn4e7");
		btn4e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e7);

		JButton btn4e8 = new JButton("");
		btn4e8.setBounds(582, 195, 25, 25);
		btn4e8.setName("btn4e8");
		btn4e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e8);

		JButton btn4e9 = new JButton("");
		btn4e9.setBounds(607, 195, 25, 25);
		btn4e9.setName("btn4e9");
		btn4e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e9);

		JButton btn4e10 = new JButton("");
		btn4e10.setBounds(632, 195, 25, 25);
		btn4e10.setName("btn4e10");
		btn4e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e10);

		JButton btn4e11 = new JButton("");
		btn4e11.setBounds(657, 195, 25, 25);
		btn4e11.setName("btn4e11");
		btn4e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e11);

		JButton btn4e12 = new JButton("");
		btn4e12.setBounds(682, 195, 25, 25);
		btn4e12.setName("btn4e12");
		btn4e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e12);

		JButton btn4e13 = new JButton("");
		btn4e13.setBounds(707, 195, 25, 25);
		btn4e13.setName("btn4e13");
		btn4e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e13);

		JButton btn4e14 = new JButton("");
		btn4e14.setBounds(732, 195, 25, 25);
		btn4e14.setName("btn4e14");
		btn4e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e14);

		JButton btn4e15 = new JButton("");
		btn4e15.setBounds(757, 195, 25, 25);
		btn4e15.setName("btn4e15");
		btn4e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn4e15);

		JButton btn5e1 = new JButton("");
		btn5e1.setBounds(407, 220, 25, 25);
		btn5e1.setName("btn5e1");
		btn5e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e1);

		JButton btn5e2 = new JButton("");
		btn5e2.setBounds(432, 220, 25, 25);
		btn5e2.setName("btn5e2");
		btn5e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e2);

		JButton btn5e3 = new JButton("");
		btn5e3.setBounds(457, 220, 25, 25);
		btn5e3.setName("btn5e3");
		btn5e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e3);

		JButton btn5e4 = new JButton("");
		btn5e4.setBounds(482, 220, 25, 25);
		btn5e4.setName("btn5e4");
		btn5e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e4);

		JButton btn5e5 = new JButton("");
		btn5e5.setBounds(507, 220, 25, 25);
		btn5e5.setName("btn5e5");
		btn5e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e5);

		JButton btn5e6 = new JButton("");
		btn5e6.setBounds(532, 220, 25, 25);
		btn5e6.setName("btn5e6");
		btn5e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e6);

		JButton btn5e7 = new JButton("");
		btn5e7.setBounds(557, 220, 25, 25);
		btn5e7.setName("btn5e7");
		btn5e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e7);

		JButton btn5e8 = new JButton("");
		btn5e8.setBounds(582, 220, 25, 25);
		btn5e8.setName("btn5e8");
		btn5e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e8);

		JButton btn5e9 = new JButton("");
		btn5e9.setBounds(607, 220, 25, 25);
		btn5e9.setName("btn5e9");
		btn5e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e9);

		JButton btn5e10 = new JButton("");
		btn5e10.setBounds(632, 220, 25, 25);
		btn5e10.setName("btn5e10");
		btn5e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e10);

		JButton btn5e11 = new JButton("");
		btn5e11.setBounds(657, 220, 25, 25);
		btn5e11.setName("btn5e11");
		btn5e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e11);

		JButton btn5e12 = new JButton("");
		btn5e12.setBounds(682, 220, 25, 25);
		btn5e12.setName("btn5e12");
		btn5e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e12);

		JButton btn5e13 = new JButton("");
		btn5e13.setBounds(707, 220, 25, 25);
		btn5e13.setName("btn5e13");
		btn5e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e13);

		JButton btn5e14 = new JButton("");
		btn5e14.setBounds(732, 220, 25, 25);
		btn5e14.setName("btn5e14");
		btn5e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e14);

		JButton btn5e15 = new JButton("");
		btn5e15.setBounds(757, 220, 25, 25);
		btn5e15.setName("btn5e15");
		btn5e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn5e15);

		JButton btn6e1 = new JButton("");
		btn6e1.setBounds(407, 245, 25, 25);
		btn6e1.setName("btn6e1");
		btn6e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e1);

		JButton btn6e2 = new JButton("");
		btn6e2.setBounds(432, 245, 25, 25);
		btn6e2.setName("btn6e2");
		btn6e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e2);

		JButton btn6e3 = new JButton("");
		btn6e3.setBounds(457, 245, 25, 25);
		btn6e3.setName("btn6e3");
		btn6e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e3);

		JButton btn6e4 = new JButton("");
		btn6e4.setBounds(482, 245, 25, 25);
		btn6e4.setName("btn6e4");
		btn6e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e4);

		JButton btn6e5 = new JButton("");
		btn6e5.setBounds(507, 245, 25, 25);
		btn6e5.setName("btn6e5");
		btn6e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e5);

		JButton btn6e6 = new JButton("");
		btn6e6.setBounds(532, 245, 25, 25);
		btn6e6.setName("btn6e6");
		btn6e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e6);

		JButton btn6e7 = new JButton("");
		btn6e7.setBounds(557, 245, 25, 25);
		btn6e7.setName("btn6e7");
		btn6e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e7);

		JButton btn6e8 = new JButton("");
		btn6e8.setBounds(582, 245, 25, 25);
		btn6e8.setName("btn6e8");
		btn6e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e8);

		JButton btn6e9 = new JButton("");
		btn6e9.setBounds(607, 245, 25, 25);
		btn6e9.setName("btn6e9");
		btn6e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e9);

		JButton btn6e10 = new JButton("");
		btn6e10.setBounds(632, 245, 25, 25);
		btn6e10.setName("btn6e10");
		btn6e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e10);

		JButton btn6e11 = new JButton("");
		btn6e11.setBounds(657, 245, 25, 25);
		btn6e11.setName("btn6e11");
		btn6e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e11);

		JButton btn6e12 = new JButton("");
		btn6e12.setBounds(682, 245, 25, 25);
		btn6e12.setName("btn6e12");
		btn6e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e12);

		JButton btn6e13 = new JButton("");
		btn6e13.setBounds(707, 245, 25, 25);
		btn6e13.setName("btn6e13");
		btn6e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e13);

		JButton btn6e14 = new JButton("");
		btn6e14.setBounds(732, 245, 25, 25);
		btn6e14.setName("btn6e14");
		btn6e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e14);

		JButton btn6e15 = new JButton("");
		btn6e15.setBounds(757, 245, 25, 25);
		btn6e15.setName("btn6e15");
		btn6e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn6e15);

		JButton btn7e1 = new JButton("");
		btn7e1.setBounds(407, 270, 25, 25);
		btn7e1.setName("btn7e1");
		btn7e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e1);

		JButton btn7e2 = new JButton("");
		btn7e2.setBounds(432, 270, 25, 25);
		btn7e2.setName("btn7e2");
		btn7e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e2);

		JButton btn7e3 = new JButton("");
		btn7e3.setBounds(457, 270, 25, 25);
		btn7e3.setName("btn7e3");
		btn7e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e3);

		JButton btn7e4 = new JButton("");
		btn7e4.setBounds(482, 270, 25, 25);
		btn7e4.setName("btn7e4");
		btn7e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e4);

		JButton btn7e5 = new JButton("");
		btn7e5.setBounds(507, 270, 25, 25);
		btn7e5.setName("btn7e5");
		btn7e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e5);

		JButton btn7e6 = new JButton("");
		btn7e6.setBounds(532, 270, 25, 25);
		btn7e6.setName("btn7e6");
		btn7e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e6);

		JButton btn7e7 = new JButton("");
		btn7e7.setBounds(557, 270, 25, 25);
		btn7e7.setName("btn7e7");
		btn7e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e7);

		JButton btn7e8 = new JButton("");
		btn7e8.setBounds(582, 270, 25, 25);
		btn7e8.setName("btn7e8");
		btn7e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e8);

		JButton btn7e9 = new JButton("");
		btn7e9.setBounds(607, 270, 25, 25);
		btn7e9.setName("btn7e9");
		btn7e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e9);

		JButton btn7e10 = new JButton("");
		btn7e10.setBounds(632, 270, 25, 25);
		btn7e10.setName("btn7e10");
		btn7e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e10);

		JButton btn7e11 = new JButton("");
		btn7e11.setBounds(657, 270, 25, 25);
		btn7e11.setName("btn7e11");
		btn7e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e11);

		JButton btn7e12 = new JButton("");
		btn7e12.setBounds(682, 270, 25, 25);
		btn7e12.setName("btn7e12");
		btn7e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e12);

		JButton btn7e13 = new JButton("");
		btn7e13.setBounds(707, 270, 25, 25);
		btn7e13.setName("btn7e13");
		btn7e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e13);

		JButton btn7e14 = new JButton("");
		btn7e14.setBounds(732, 270, 25, 25);
		btn7e14.setName("btn7e14");
		btn7e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e14);

		JButton btn7e15 = new JButton("");
		btn7e15.setBounds(757, 270, 25, 25);
		btn7e15.setName("btn7e15");
		btn7e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn7e15);

		JButton btn8e1 = new JButton("");
		btn8e1.setBounds(407, 295, 25, 25);
		btn8e1.setName("btn8e1");
		btn8e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e1);

		JButton btn8e2 = new JButton("");
		btn8e2.setBounds(432, 295, 25, 25);
		btn8e2.setName("btn8e2");
		btn8e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e2);

		JButton btn8e3 = new JButton("");
		btn8e3.setBounds(457, 295, 25, 25);
		btn8e3.setName("btn8e3");
		btn8e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e3);

		JButton btn8e4 = new JButton("");
		btn8e4.setBounds(482, 295, 25, 25);
		btn8e4.setName("btn8e4");
		btn8e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e4);

		JButton btn8e5 = new JButton("");
		btn8e5.setBounds(507, 295, 25, 25);
		btn8e5.setName("btn8e5");
		btn8e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e5);

		JButton btn8e6 = new JButton("");
		btn8e6.setBounds(532, 295, 25, 25);
		btn8e6.setName("btn8e6");
		btn8e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e6);

		JButton btn8e7 = new JButton("");
		btn8e7.setBounds(557, 295, 25, 25);
		btn8e7.setName("btn8e7");
		btn8e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e7);

		JButton btn8e8 = new JButton("");
		btn8e8.setBounds(582, 295, 25, 25);
		btn8e8.setName("btn8e8");
		btn8e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e8);

		JButton btn8e9 = new JButton("");
		btn8e9.setBounds(607, 295, 25, 25);
		btn8e9.setName("btn8e9");
		btn8e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e9);

		JButton btn8e10 = new JButton("");
		btn8e10.setBounds(632, 295, 25, 25);
		btn8e10.setName("btn8e10");
		btn8e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e10);

		JButton btn8e11 = new JButton("");
		btn8e11.setBounds(657, 295, 25, 25);
		btn8e11.setName("btn8e11");
		btn8e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e11);

		JButton btn8e12 = new JButton("");
		btn8e12.setBounds(682, 295, 25, 25);
		btn8e12.setName("btn8e12");
		btn8e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e12);

		JButton btn8e13 = new JButton("");
		btn8e13.setBounds(707, 295, 25, 25);
		btn8e13.setName("btn8e13");
		btn8e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e13);

		JButton btn8e14 = new JButton("");
		btn8e14.setBounds(732, 295, 25, 25);
		btn8e14.setName("btn8e14");
		btn8e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e14);

		JButton btn8e15 = new JButton("");
		btn8e15.setBounds(757, 295, 25, 25);
		btn8e15.setName("btn8e15");
		btn8e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn8e15);

		JButton btn9e1 = new JButton("");
		btn9e1.setBounds(407, 320, 25, 25);
		btn9e1.setName("btn9e1");
		btn9e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e1);

		JButton btn9e2 = new JButton("");
		btn9e2.setBounds(432, 320, 25, 25);
		btn9e2.setName("btn9e2");
		btn9e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e2);

		JButton btn9e3 = new JButton("");
		btn9e3.setBounds(457, 320, 25, 25);
		btn9e3.setName("btn9e3");
		btn9e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e3);

		JButton btn9e4 = new JButton("");
		btn9e4.setBounds(482, 320, 25, 25);
		btn9e4.setName("btn9e4");
		btn9e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e4);

		JButton btn9e5 = new JButton("");
		btn9e5.setBounds(507, 320, 25, 25);
		btn9e5.setName("btn9e5");
		btn9e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e5);

		JButton btn9e6 = new JButton("");
		btn9e6.setBounds(532, 320, 25, 25);
		btn9e6.setName("btn9e6");
		btn9e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e6);

		JButton btn9e7 = new JButton("");
		btn9e7.setBounds(557, 320, 25, 25);
		btn9e7.setName("btn9e7");
		btn9e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e7);

		JButton btn9e8 = new JButton("");
		btn9e8.setBounds(582, 320, 25, 25);
		btn9e8.setName("btn9e8");
		btn9e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e8);

		JButton btn9e9 = new JButton("");
		btn9e9.setBounds(607, 320, 25, 25);
		btn9e9.setName("btn9e9");
		btn9e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e9);

		JButton btn9e10 = new JButton("");
		btn9e10.setBounds(632, 320, 25, 25);
		btn9e10.setName("btn9e10");
		btn9e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e10);

		JButton btn9e11 = new JButton("");
		btn9e11.setBounds(657, 320, 25, 25);
		btn9e11.setName("btn9e11");
		btn9e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e11);

		JButton btn9e12 = new JButton("");
		btn9e12.setBounds(682, 320, 25, 25);
		btn9e12.setName("btn9e12");
		btn9e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e12);

		JButton btn9e13 = new JButton("");
		btn9e13.setBounds(707, 320, 25, 25);
		btn9e13.setName("btn9e13");
		btn9e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e13);

		JButton btn9e14 = new JButton("");
		btn9e14.setBounds(732, 320, 25, 25);
		btn9e14.setName("btn9e14");
		btn9e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e14);

		JButton btn9e15 = new JButton("");
		btn9e15.setBounds(757, 320, 25, 25);
		btn9e15.setName("btn9e15");
		btn9e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn9e15);

		JButton btn10e1 = new JButton("");
		btn10e1.setBounds(407, 345, 25, 25);
		btn10e1.setName("btn10e1");
		btn10e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e1);

		JButton btn10e2 = new JButton("");
		btn10e2.setBounds(432, 345, 25, 25);
		btn10e2.setName("btn10e2");
		btn10e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e2);

		JButton btn10e3 = new JButton("");
		btn10e3.setBounds(457, 345, 25, 25);
		btn10e3.setName("btn10e3");
		btn10e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e3);

		JButton btn10e4 = new JButton("");
		btn10e4.setBounds(482, 345, 25, 25);
		btn10e4.setName("btn10e4");
		btn10e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e4);

		JButton btn10e5 = new JButton("");
		btn10e5.setBounds(507, 345, 25, 25);
		btn10e5.setName("btn10e5");
		btn10e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e5);

		JButton btn10e6 = new JButton("");
		btn10e6.setBounds(532, 345, 25, 25);
		btn10e6.setName("btn10e6");
		btn10e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e6);

		JButton btn10e7 = new JButton("");
		btn10e7.setBounds(557, 345, 25, 25);
		btn10e7.setName("btn10e7");
		btn10e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e7);

		JButton btn10e8 = new JButton("");
		btn10e8.setBounds(582, 345, 25, 25);
		btn10e8.setName("btn10e8");
		btn10e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e8);

		JButton btn10e9 = new JButton("");
		btn10e9.setBounds(607, 345, 25, 25);
		btn10e9.setName("btn10e9");
		btn10e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e9);

		JButton btn10e10 = new JButton("");
		btn10e10.setBounds(632, 345, 25, 25);
		btn10e10.setName("btn10e10");
		btn10e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e10);

		JButton btn10e11 = new JButton("");
		btn10e11.setBounds(657, 345, 25, 25);
		btn10e11.setName("btn10e11");
		btn10e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e11);

		JButton btn10e12 = new JButton("");
		btn10e12.setBounds(682, 345, 25, 25);
		btn10e12.setName("btn10e12");
		btn10e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e12);

		JButton btn10e13 = new JButton("");
		btn10e13.setBounds(707, 345, 25, 25);
		btn10e13.setName("btn10e13");
		btn10e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e13);

		JButton btn10e14 = new JButton("");
		btn10e14.setBounds(732, 345, 25, 25);
		btn10e14.setName("btn10e14");
		btn10e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e14);

		JButton btn10e15 = new JButton("");
		btn10e15.setBounds(757, 345, 25, 25);
		btn10e15.setName("btn10e15");
		btn10e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn10e15);

		JButton btn11e1 = new JButton("");
		btn11e1.setBounds(407, 370, 25, 25);
		btn11e1.setName("btn11e1");
		btn11e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e1);

		JButton btn11e2 = new JButton("");
		btn11e2.setBounds(432, 370, 25, 25);
		btn11e2.setName("btn11e2");
		btn11e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e2);

		JButton btn11e3 = new JButton("");
		btn11e3.setBounds(457, 370, 25, 25);
		btn11e3.setName("btn11e3");
		btn11e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e3);

		JButton btn11e4 = new JButton("");
		btn11e4.setBounds(482, 370, 25, 25);
		btn11e4.setName("btn11e4");
		btn11e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e4);

		JButton btn11e5 = new JButton("");
		btn11e5.setBounds(507, 370, 25, 25);
		btn11e5.setName("btn11e5");
		btn11e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e5);

		JButton btn11e6 = new JButton("");
		btn11e6.setBounds(532, 370, 25, 25);
		btn11e6.setName("btn11e6");
		btn11e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e6);

		JButton btn11e7 = new JButton("");
		btn11e7.setBounds(557, 370, 25, 25);
		btn11e7.setName("btn11e7");
		btn11e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e7);

		JButton btn11e8 = new JButton("");
		btn11e8.setBounds(582, 370, 25, 25);
		btn11e8.setName("btn11e8");
		btn11e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e8);

		JButton btn11e9 = new JButton("");
		btn11e9.setBounds(607, 370, 25, 25);
		btn11e9.setName("btn11e9");
		btn11e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e9);

		JButton btn11e10 = new JButton("");
		btn11e10.setBounds(632, 370, 25, 25);
		btn11e10.setName("btn11e10");
		btn11e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e10);

		JButton btn11e11 = new JButton("");
		btn11e11.setBounds(657, 370, 25, 25);
		btn11e11.setName("btn11e11");
		btn11e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e11);

		JButton btn11e12 = new JButton("");
		btn11e12.setBounds(682, 370, 25, 25);
		btn11e12.setName("btn11e12");
		btn11e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e12);

		JButton btn11e13 = new JButton("");
		btn11e13.setBounds(707, 370, 25, 25);
		btn11e13.setName("btn11e13");
		btn11e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e13);

		JButton btn11e14 = new JButton("");
		btn11e14.setBounds(732, 370, 25, 25);
		btn11e14.setName("btn11e14");
		btn11e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e14);

		JButton btn11e15 = new JButton("");
		btn11e15.setBounds(757, 370, 25, 25);
		btn11e15.setName("btn11e15");
		btn11e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn11e15);

		JButton btn12e1 = new JButton("");
		btn12e1.setBounds(407, 395, 25, 25);
		btn12e1.setName("btn12e1");
		btn12e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e1);

		JButton btn12e2 = new JButton("");
		btn12e2.setBounds(432, 395, 25, 25);
		btn12e2.setName("btn12e2");
		btn12e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e2);

		JButton btn12e3 = new JButton("");
		btn12e3.setBounds(457, 395, 25, 25);
		btn12e3.setName("btn12e3");
		btn12e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e3);

		JButton btn12e4 = new JButton("");
		btn12e4.setBounds(482, 395, 25, 25);
		btn12e4.setName("btn12e4");
		btn12e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e4);

		JButton btn12e5 = new JButton("");
		btn12e5.setBounds(507, 395, 25, 25);
		btn12e5.setName("btn12e5");
		btn12e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e5);

		JButton btn12e6 = new JButton("");
		btn12e6.setBounds(532, 395, 25, 25);
		btn12e6.setName("btn12e6");
		btn12e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e6);

		JButton btn12e7 = new JButton("");
		btn12e7.setBounds(557, 395, 25, 25);
		btn12e7.setName("btn12e7");
		btn12e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e7);

		JButton btn12e8 = new JButton("");
		btn12e8.setBounds(582, 395, 25, 25);
		btn12e8.setName("btn12e8");
		btn12e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e8);

		JButton btn12e9 = new JButton("");
		btn12e9.setBounds(607, 395, 25, 25);
		btn12e9.setName("btn12e9");
		btn12e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e9);

		JButton btn12e10 = new JButton("");
		btn12e10.setBounds(632, 395, 25, 25);
		btn12e10.setName("btn12e10");
		btn12e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e10);

		JButton btn12e11 = new JButton("");
		btn12e11.setBounds(657, 395, 25, 25);
		btn12e11.setName("btn12e11");
		btn12e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e11);

		JButton btn12e12 = new JButton("");
		btn12e12.setBounds(682, 395, 25, 25);
		btn12e12.setName("btn12e12");
		btn12e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e12);

		JButton btn12e13 = new JButton("");
		btn12e13.setBounds(707, 395, 25, 25);
		btn12e13.setName("btn12e13");
		btn12e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e13);

		JButton btn12e14 = new JButton("");
		btn12e14.setBounds(732, 395, 25, 25);
		btn12e14.setName("btn12e14");
		btn12e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e14);

		JButton btn12e15 = new JButton("");
		btn12e15.setBounds(757, 395, 25, 25);
		btn12e15.setName("btn12e15");
		btn12e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn12e15);

		JButton btn13e1 = new JButton("");
		btn13e1.setBounds(407, 420, 25, 25);
		btn13e1.setName("btn13e1");
		btn13e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e1);

		JButton btn13e2 = new JButton("");
		btn13e2.setBounds(432, 420, 25, 25);
		btn13e2.setName("btn13e2");
		btn13e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e2);

		JButton btn13e3 = new JButton("");
		btn13e3.setBounds(457, 420, 25, 25);
		btn13e3.setName("btn13e3");
		btn13e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e3);

		JButton btn13e4 = new JButton("");
		btn13e4.setBounds(482, 420, 25, 25);
		btn13e4.setName("btn13e4");
		btn13e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e4);

		JButton btn13e5 = new JButton("");
		btn13e5.setBounds(507, 420, 25, 25);
		btn13e5.setName("btn13e5");
		btn13e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e5);

		JButton btn13e6 = new JButton("");
		btn13e6.setBounds(532, 420, 25, 25);
		btn13e6.setName("btn13e6");
		btn13e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e6);

		JButton btn13e7 = new JButton("");
		btn13e7.setBounds(557, 420, 25, 25);
		btn13e7.setName("btn13e7");
		btn13e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e7);

		JButton btn13e8 = new JButton("");
		btn13e8.setBounds(582, 420, 25, 25);
		btn13e8.setName("btn13e8");
		btn13e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e8);

		JButton btn13e9 = new JButton("");
		btn13e9.setBounds(607, 420, 25, 25);
		btn13e9.setName("btn13e9");
		btn13e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e9);

		JButton btn13e10 = new JButton("");
		btn13e10.setBounds(632, 420, 25, 25);
		btn13e10.setName("btn13e10");
		btn13e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e10);

		JButton btn13e11 = new JButton("");
		btn13e11.setBounds(657, 420, 25, 25);
		btn13e11.setName("btn13e11");
		btn13e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e11);

		JButton btn13e12 = new JButton("");
		btn13e12.setBounds(682, 420, 25, 25);
		btn13e12.setName("btn13e12");
		btn13e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e12);

		JButton btn13e13 = new JButton("");
		btn13e13.setBounds(707, 420, 25, 25);
		btn13e13.setName("btn13e13");
		btn13e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e13);

		JButton btn13e14 = new JButton("");
		btn13e14.setBounds(732, 420, 25, 25);
		btn13e14.setName("btn13e14");
		btn13e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e14);

		JButton btn13e15 = new JButton("");
		btn13e15.setBounds(757, 420, 25, 25);
		btn13e15.setName("btn13e15");
		btn13e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn13e15);

		JButton btn14e1 = new JButton("");
		btn14e1.setBounds(407, 445, 25, 25);
		btn14e1.setName("btn14e1");
		btn14e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e1);

		JButton btn14e2 = new JButton("");
		btn14e2.setBounds(432, 445, 25, 25);
		btn14e2.setName("btn14e2");
		btn14e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e2);

		JButton btn14e3 = new JButton("");
		btn14e3.setBounds(457, 445, 25, 25);
		btn14e3.setName("btn14e3");
		btn14e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e3);

		JButton btn14e4 = new JButton("");
		btn14e4.setBounds(482, 445, 25, 25);
		btn14e4.setName("btn14e4");
		btn14e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e4);

		JButton btn14e5 = new JButton("");
		btn14e5.setBounds(507, 445, 25, 25);
		btn14e5.setName("btn14e5");
		btn14e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e5);

		JButton btn14e6 = new JButton("");
		btn14e6.setBounds(532, 445, 25, 25);
		btn14e6.setName("btn14e6");
		btn14e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e6);

		JButton btn14e7 = new JButton("");
		btn14e7.setBounds(557, 445, 25, 25);
		btn14e7.setName("btn14e7");
		btn14e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e7);

		JButton btn14e8 = new JButton("");
		btn14e8.setBounds(582, 445, 25, 25);
		btn14e8.setName("btn14e8");
		btn14e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e8);

		JButton btn14e9 = new JButton("");
		btn14e9.setBounds(607, 445, 25, 25);
		btn14e9.setName("btn14e9");
		btn14e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e9);

		JButton btn14e10 = new JButton("");
		btn14e10.setBounds(632, 445, 25, 25);
		btn14e10.setName("btn14e10");
		btn14e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e10);

		JButton btn14e11 = new JButton("");
		btn14e11.setBounds(657, 445, 25, 25);
		btn14e11.setName("btn14e11");
		btn14e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e11);

		JButton btn14e12 = new JButton("");
		btn14e12.setBounds(682, 445, 25, 25);
		btn14e12.setName("btn14e12");
		btn14e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e12);

		JButton btn14e13 = new JButton("");
		btn14e13.setBounds(707, 445, 25, 25);
		btn14e13.setName("btn14e13");
		btn14e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e13);

		JButton btn14e14 = new JButton("");
		btn14e14.setBounds(732, 445, 25, 25);
		btn14e14.setName("btn14e14");
		btn14e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e14);

		JButton btn14e15 = new JButton("");
		btn14e15.setBounds(757, 445, 25, 25);
		btn14e15.setName("btn14e15");
		btn14e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn14e15);

		JButton btn15e1 = new JButton("");
		btn15e1.setBounds(407, 470, 25, 25);
		btn15e1.setName("btn15e1");
		btn15e1.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e1);

		JButton btn15e2 = new JButton("");
		btn15e2.setBounds(432, 470, 25, 25);
		btn15e2.setName("btn15e2");
		btn15e2.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e2);

		JButton btn15e3 = new JButton("");
		btn15e3.setBounds(457, 470, 25, 25);
		btn15e3.setName("btn15e3");
		btn15e3.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e3);

		JButton btn15e4 = new JButton("");
		btn15e4.setBounds(482, 470, 25, 25);
		btn15e4.setName("btn15e4");
		btn15e4.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e4);

		JButton btn15e5 = new JButton("");
		btn15e5.setBounds(507, 470, 25, 25);
		btn15e5.setName("btn15e5");
		btn15e5.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e5);

		JButton btn15e6 = new JButton("");
		btn15e6.setBounds(532, 470, 25, 25);
		btn15e6.setName("btn15e6");
		btn15e6.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e6);

		JButton btn15e7 = new JButton("");
		btn15e7.setBounds(557, 470, 25, 25);
		btn15e7.setName("btn15e7");
		btn15e7.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e7);

		JButton btn15e8 = new JButton("");
		btn15e8.setBounds(582, 470, 25, 25);
		btn15e8.setName("btn15e8");
		btn15e8.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e8);

		JButton btn15e9 = new JButton("");
		btn15e9.setBounds(607, 470, 25, 25);
		btn15e9.setName("btn15e9");
		btn15e9.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e9);

		JButton btn15e10 = new JButton("");
		btn15e10.setBounds(632, 470, 25, 25);
		btn15e10.setName("btn15e10");
		btn15e10.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e10);

		JButton btn15e11 = new JButton("");
		btn15e11.setBounds(657, 470, 25, 25);
		btn15e11.setName("btn15e11");
		btn15e11.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e11);

		JButton btn15e12 = new JButton("");
		btn15e12.setBounds(682, 470, 25, 25);
		btn15e12.setName("btn15e12");
		btn15e12.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e12);

		JButton btn15e13 = new JButton("");
		btn15e13.setBounds(707, 470, 25, 25);
		btn15e13.setName("btn15e13");
		btn15e13.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e13);

		JButton btn15e14 = new JButton("");
		btn15e14.setBounds(732, 470, 25, 25);
		btn15e14.setName("btn15e14");
		btn15e14.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e14);

		JButton btn15e15 = new JButton("");
		btn15e15.setBounds(757, 470, 25, 25);
		btn15e15.setName("btn15e15");
		btn15e15.addActionListener(enemyGridListener);
		panelEnemyGrid.add(btn15e15);
	}

	/**
	 * Mouse event listener for the ship buttons.
	 */
	ActionListener shipListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			ResetButtonColor(panelShip);
			((JButton) e.getSource()).setForeground(new Color(186, 85, 211));
			switch (((JButton) e.getSource()).getName()) {
			case "btnCarrier":
				SelectShip("Carrier", (byte) 5);
				break;
			case "btnCruiser":
				SelectShip("Cruiser", (byte) 3);
				break;
			case "btnBattleship":
				SelectShip("Battleship", (byte) 4);
				break;
			case "btnDestroyer":
				SelectShip("Destroyer", (byte) 2);
				break;
			case "btnSubmarine":
				SelectShip("Submarine", (byte) 3);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Prepare selected ship to be placed.
	 */
	private void SelectShip(String name, byte size) {
		if (frmBattleship.getCursor().getType() == Cursor.DEFAULT_CURSOR) {
			frmBattleship.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
			activeShipOrientation = Orientation.HORIZONTAL.getValue();
		}
		activeShipName = name;
		activeShipSize = size;
	}

	/**
	 * Mouse event listener for the rotate button.
	 */
	ActionListener rotateListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (frmBattleship.getCursor().getType()) {
			case Cursor.W_RESIZE_CURSOR:
				frmBattleship.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
				activeShipOrientation = Orientation.VERTICAL.getValue();
				break;
			case Cursor.N_RESIZE_CURSOR:
				frmBattleship.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
				activeShipOrientation = Orientation.HORIZONTAL.getValue();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Mouse event listener for the start button.
	 */
	ActionListener startListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int enabledBtnCount = 0;
			for (Component btn : panelShip.getComponents()) {
				if (btn.isEnabled()) {
					enabledBtnCount++;
				}
			}
			if (enabledBtnCount == 1) {
				String tempIP = textIP.getText().trim();
				boolean isConnected = tcpHelper.StartConnection(frmBattleship, tempIP);
				if (isConnected) {
					for (Component btn : panelStart.getComponents()) {
						btn.setEnabled(false);
					}
					if (tempIP.equals("")) {
						isMyTurn = true;
						JOptionPane.showMessageDialog(frmBattleship, "You start first.");
					} else {
						isMyTurn = false;
						ReceiveAttackInfo();
						JOptionPane.showMessageDialog(frmBattleship, "Opponent starts first.");
					}
					for (Component btn : panelBomb.getComponents()) {
						btn.setEnabled(true);
					}
				}
			} else {
				JOptionPane.showMessageDialog(frmBattleship, "You have to place all the ships before you start.",
						"Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	};

	/**
	 * Mouse event listener for the firing buttons.
	 */
	ActionListener fireListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			ResetButtonColor(panelBomb);
			((JButton) e.getSource()).setForeground(new Color(186, 85, 211));
			switch (((JButton) e.getSource()).getName()) {
			case "btnFire":
				SelectBomb("Fire", true, (byte) 0);
				break;
			case "btnProbe":
				SelectBomb("Probe", false, (byte) 3);
				break;
			case "btnBomb":
				SelectBomb("Bomb", true, (byte) 1);
				break;
			case "btnHbomb":
				SelectBomb("Hbomb", true, (byte) 2);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Prepare selected bomb to be fired.
	 */
	private void SelectBomb(String name, boolean inflictsDamage, byte radius) {
		frmBattleship.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		activeBombName = name;
		activeBombRadius = radius;
		activeBombInflictsDamage = inflictsDamage;
	}

	/**
	 * Mouse event listener for the player grid.
	 */
	ActionListener playerGridListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!activeShipName.equals("")) {
				String tempText = ((JButton) e.getSource()).getName().substring(3,
						((JButton) e.getSource()).getName().length());
				int startX = Integer.valueOf(tempText.split("p")[1]);
				int startY = Integer.valueOf(tempText.split("p")[0]);
				Boolean isValid = bsHelper.PlaceShip(startX - 1, startY - 1, activeShipOrientation, activeShipName,
						activeShipSize);
				if (isValid) {
					PutShipOnGrid(startX, startY);
					DeselectShip();
				}
			}
		}
	};

	/**
	 * Place ship on the UI grid.
	 */
	private void PutShipOnGrid(int startX, int startY) {
		int startIndex = (activeShipOrientation == Orientation.HORIZONTAL.getValue()) ? startX : startY;
		for (int i = startIndex; i < startIndex + activeShipSize; i++) {
			String tempBtnName = "btn";
			if (activeShipOrientation == Orientation.HORIZONTAL.getValue()) {
				tempBtnName += startY + "p" + i;
			} else {
				tempBtnName += i + "p" + startX;
			}
			for (Component btn : panelPlayerGrid.getComponents()) {
				if (btn.getName().equals(tempBtnName)) {
					btn.setBackground(bsHelper.GetShipColor(activeShipName));
					((JButton) btn).setText(activeShipName);
					((JButton) btn).setToolTipText(activeShipName);
					break;
				}
			}
		}
	}

	/**
	 * Use ship and return back to normal state.
	 */
	private void DeselectShip() {
		for (Component btn : panelShip.getComponents()) {
			if (btn.getName().contains(activeShipName)) {
				String btnText = ((JButton) btn).getText();
				int count = Character.getNumericValue(btnText.charAt(btnText.length() - 2)) - 1;
				btnText = btnText.substring(0, btnText.length() - 2) + count + ")";
				((JButton) btn).setText(btnText);
				if (count == 0) {
					((JButton) btn).setEnabled(false);
				}
				break;
			}
		}
		frmBattleship.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		ResetButtonColor(panelShip);
		activeShipName = "";
		activeShipSize = 0;
		activeShipOrientation = Orientation.HORIZONTAL.getValue();
	}

	/**
	 * Mouse event listener for the enemy grid.
	 */
	ActionListener enemyGridListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (!activeBombName.equals("") && isMyTurn) {
				String tempText = ((JButton) e.getSource()).getName().substring(3,
						((JButton) e.getSource()).getName().length());
				int startX = Integer.valueOf(tempText.split("e")[1]);
				int startY = Integer.valueOf(tempText.split("e")[0]);
				AttackInfo atkInfo = new AttackInfo(startX, startY, activeBombRadius, activeBombInflictsDamage);
				SendAttackInfo(atkInfo);
				DeselectBomb();
			}
		}
	};

	/**
	 * Use bomb and return back to normal state.
	 */
	private void DeselectBomb() {
		for (Component btn : panelBomb.getComponents()) {
			if (btn.getName().contains(activeBombName) && !btn.getName().contains("Fire")) {
				String btnText = ((JButton) btn).getText();
				int count = Character.getNumericValue(btnText.charAt(btnText.length() - 2)) - 1;
				btnText = btnText.substring(0, btnText.length() - 2) + count + ")";
				((JButton) btn).setText(btnText);
				if (count == 0) {
					((JButton) btn).setEnabled(false);
				}
				break;
			}
		}
		frmBattleship.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		ResetButtonColor(panelBomb);
		isMyTurn = false;
		activeBombName = "";
		activeBombRadius = 0;
		activeBombInflictsDamage = false;
	}

	/**
	 * Reset foreground colour of buttons.
	 */
	private void ResetButtonColor(JPanel panel) {
		for (Component btn : panel.getComponents()) {
			btn.setForeground(Color.DARK_GRAY);
		}
	}

	/**
	 * Receive attack info from enemy.
	 */
	private void ReceiveAttackInfo() {
		tcpHelper.ReceiveAttackInfo(this);
	}

	/**
	 * Send attack info to enemy.
	 */
	private void SendAttackInfo(AttackInfo attackInfo) {
		tcpHelper.SendAttackInfo(this, attackInfo);
	}

	/**
	 * Determine attack result.
	 */
	public List<AttackResult> ResolveAttackResult(AttackInfo attackInfo) {
		return bsHelper.ResolveAttackResult(attackInfo);
	}

	/**
	 * Draws outgoing attack result.
	 */
	public void DrawAttackResult(List<AttackResult> atkResultList) {
		for (AttackResult result : atkResultList) {
			String tempBtnName = "btn" + result.coordY + "e" + result.coordX;
			for (Component btn : panelEnemyGrid.getComponents()) {
				if (btn.getName().equals(tempBtnName)) {
					if (result.inflictsDamage) {
						btn.setBackground(new Color(255, 99, 71));
					} else {
						btn.setBackground(new Color(255, 250, 205));
					}
					if (result.isHit) {
						((JButton) btn).setText("Hit");
						((JButton) btn).setToolTipText("Hit");
					}
					if (result.inflictsDamage && result.isHit) {
						killCount += 1;
					}
					break;
				}
			}
		}
		if (killCount == 22) {
			JOptionPane.showMessageDialog(frmBattleship, "You Won!");
			tcpHelper.CloseSocket();
		} else {
			ReceiveAttackInfo();
		}
	}

	/**
	 * Draws incoming attack result.
	 */
	public void DrawIncomingAttackResult(List<AttackResult> atkResultList) {
		for (AttackResult result : atkResultList) {
			if (result.inflictsDamage) {
				String tempBtnName = "btn" + result.coordY + "p" + result.coordX;
				for (Component btn : panelPlayerGrid.getComponents()) {
					if (btn.getName().equals(tempBtnName)) {
						btn.setBackground(new Color(255, 99, 71));
						if (result.isHit) {
							deadCount += 1;
						}
						break;
					}
				}
			}
		}
		if (deadCount == 22) {
			JOptionPane.showMessageDialog(frmBattleship, "You Lost!");
			tcpHelper.CloseSocket();
		} else {
			isMyTurn = true;
		}
	}
}

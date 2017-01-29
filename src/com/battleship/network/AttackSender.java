package com.battleship.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.battleship.gui.BattleshipGUI;

public class AttackSender extends SwingWorker<List<AttackResult>, Object> {

	private BattleshipGUI gui = null;
	private ObjectInputStream input = null;
	private ObjectOutputStream output = null;
	private AttackInfo attackInfo = null;

	public AttackSender(BattleshipGUI gui, ObjectInputStream input, ObjectOutputStream output, AttackInfo attackInfo) {
		this.attackInfo = attackInfo;
		this.input = input;
		this.output = output;
		this.gui = gui;
	}

	@Override
	protected List<AttackResult> doInBackground() throws Exception {
		output.writeObject(attackInfo);
		output.flush();
		AttackResultWrapper attackWrapper = (AttackResultWrapper) input.readObject();
		return attackWrapper.attackResult;
	}

	@Override
	protected void done() {
		try {
			List<AttackResult> result = this.get();
			gui.DrawAttackResult(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}

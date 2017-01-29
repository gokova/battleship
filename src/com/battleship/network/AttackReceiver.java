package com.battleship.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.battleship.gui.BattleshipGUI;

public class AttackReceiver extends SwingWorker<List<AttackResult>, Object> {

	private BattleshipGUI gui = null;
	private ObjectInputStream input = null;
	private ObjectOutputStream output = null;

	public AttackReceiver(BattleshipGUI gui, ObjectInputStream input, ObjectOutputStream output) {
		this.gui = gui;
		this.input = input;
		this.output = output;
	}

	@Override
	protected List<AttackResult> doInBackground() throws Exception {
		AttackInfo attackInfo = (AttackInfo) input.readObject();
		List<AttackResult> result = gui.ResolveAttackResult(attackInfo);
		output.writeObject(new AttackResultWrapper(result));
		output.flush();
		return result;
	}

	@Override
	protected void done() {
		try {
			List<AttackResult> result = this.get();
			gui.DrawIncomingAttackResult(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}

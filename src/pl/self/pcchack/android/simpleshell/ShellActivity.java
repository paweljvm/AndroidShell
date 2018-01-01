package pl.self.pcchack.android.simpleshell;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import pl.pcchack.activity.AbstractActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ShellActivity extends AbstractActivity {
	private TextView commandResultText;
	private EditText commandEdit;
	@Override
	protected void initFieldsAndServices(Bundle savedState) {
		commandResultText = getTextView(R.id.comand_result_text);
		commandEdit = getEditText(R.id.command_edit);
		
	}

	@Override
	protected int getMainViewId() {
		// TODO Auto-generated method stub
		return R.layout.main_layout;
	}

	@Override
	protected void saveDataInPreferences() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isKeepScreenOn() {
		// TODO Auto-generated method stub
		return false;
	}

	public void runCommand(View view) {
		try {
		Process process = Runtime.getRuntime().exec(commandEdit.getText().toString());
		ProcessKiller killer = new ProcessKiller(process, 5000);
		new Thread(killer).start();
		process.waitFor();
		killer.setKill(false);
		StringBuffer result = new StringBuffer();
		fillResultWithStream("----STANDARD OUTPUT----\n",result, process.getInputStream());
		fillResultWithStream("----ERROR OUTPUT----\n",result,process.getErrorStream());
		commandResultText.setText(result.toString());
		} catch(Exception e) {
			showToast(e.getMessage());
		}
	}
	private void fillResultWithStream(String title,StringBuffer result,InputStream stream) throws Exception {
		BufferedReader reader  = new BufferedReader(new InputStreamReader(stream));
		String line = "";
		result.append(title);
		while( (line = reader.readLine()) != null) {
			result.append(line).append("\n");
		}
		
	}
	
	private class ProcessKiller implements Runnable {
		private Process process;
		private long timeLimit;
		private boolean kill=true;
		public void setKill(boolean kill) {
			this.kill = kill;
		}

		public ProcessKiller(Process process, long timeLimit) {
			super();
			this.process = process;
			this.timeLimit = timeLimit;
		}

		@Override
		public void run() {
			while(timeLimit > 0) {
				try {
					Thread.sleep(timeLimit);
					timeLimit =0;
					if(kill)
						process.destroy();
				} catch (InterruptedException e) {
					e.printStackTrace();
					
				}
				
				
			}
		}
		
	}
}

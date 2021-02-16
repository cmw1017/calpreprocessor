package aermod;

import java.io.*;
import java.util.*;

import javax.swing.JLabel;

public class AERMOD implements Runnable{
	
	String matter;
	String[] seires = {"1", "24", "an"};
	String base_path;
	JLabel produce;
	JLabel count;
	Process process = null;
	ProcessBuilder processBuilder = null;
	ThreadInfo t_info;
	int index_thread;
	
	// base_path : 기본 파일이 넣어져 있는 경로(에어모드 실행파일, inp base 파일 등..)
	// matter : 모델링 할 물질 명
	// produce : 진행 상태를 알려주는 상자
	// count : 현재 모델링 진행 횟수를 알려주는 상자
	// t_info : 모든 쓰레드의 정보 및 상태를 가지고 있는 클래스
	// index_thread : 쓰레드의 사용 상태를 알려주는 변수
	public AERMOD(String base_path, String matter, JLabel  produce, JLabel count, ThreadInfo t_info, int index_thread) {
		this.base_path = base_path;
		this.matter = matter;
		this.produce = produce;
		this.count = count;
		this.t_info = t_info;
		this.index_thread = index_thread;
	}
	
	public void ReadyProcess() throws InterruptedException, IOException{
		
		process = new ProcessBuilder("cmd", "/c", "mkdir", base_path + "\\run\\" + matter).start();
		process = new ProcessBuilder("cmd", "/c", "copy", base_path + "\\3aermod.exe", base_path + "\\run\\" + matter + "\\").start();
		process = new ProcessBuilder("cmd", "/c", "copy", base_path + "\\aermod.bat", base_path + "\\run\\" + matter + "\\").start();
		process = new ProcessBuilder("cmd", "/c", "copy", base_path + "\\run\\aermod_" + matter + ".inp", base_path + "\\run\\" + matter + "\\").start();
		process.waitFor();
		process = new ProcessBuilder("cmd", "/c", "move", base_path + "\\run\\" +matter + "\\aermod_" + matter + ".inp", base_path + "\\run\\" +matter + "\\aermod.inp").start();
		process = new ProcessBuilder("cmd", "/c", "copy", base_path + "\\AERMOD.PFL", base_path + "\\run\\" + matter + "\\").start();
		process = new ProcessBuilder("cmd", "/c", "copy", base_path + "\\AERMOD.SFC", base_path + "\\run\\" + matter + "\\").start();
		process = new ProcessBuilder("cmd", "/c", "copy", base_path + "\\POINT_" + matter + ".dat", base_path + "\\run\\" + matter + "\\").start();
		process = new ProcessBuilder("cmd", "/c", "copy", base_path + "\\receptor_input.dat", base_path + "\\run\\" + matter + "\\").start();
		process.waitFor();
		process.destroy();
	}
	public void RunProcess() throws IOException, InterruptedException {


		File aermod = new File(base_path + "\\run\\" + matter + "\\aermod.bat");
		File aermoddir = new File(base_path + "\\run\\" + matter);
		String str = null;

		String aermodsrc = aermod.getAbsolutePath();

		if (aermod.isFile()) {
			List<String> cmd = new ArrayList<String>();
			cmd.add(aermodsrc);
			processBuilder = new ProcessBuilder(cmd);
			processBuilder.directory(aermoddir);
			process = processBuilder.start();
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			while((str = stdOut.readLine()) != null) {
				if (str.contains("+Now Processing Data For Day No.")) {
					str = str.substring(str.lastIndexOf("No.") + 5, str.length());
					str = str.substring(0, 3);
					produce.setText(str + "/365");
				}
			}
			process.waitFor();
			process.destroy();
			count.setText(String.valueOf(Integer.parseInt(count.getText()) + 1));
		} else {
			System.out.println("에러 : 모델링 실행파일이 없습니다.(3aermod.exe)");
			return;
		}
	}
	
	public void FinishProcess() throws IOException, InterruptedException {
		process = new ProcessBuilder("cmd", "/c", "mkdir", base_path + "\\res\\" + matter).start();
		process.waitFor();
		process = new ProcessBuilder("cmd", "/c", "copy", base_path + "\\run\\" +matter + "\\" + matter + "_an.FIL", base_path + "\\res\\" +matter + "\\" + matter + "_an.FIL").start();
		process.waitFor();
		process = new ProcessBuilder("cmd", "/c", "copy", base_path + "\\run\\" +matter + "\\" + matter + "_24.FIL", base_path + "\\res\\" +matter + "\\" + matter + "_24.FIL").start();
		process.waitFor();
		process = new ProcessBuilder("cmd", "/c", "copy", base_path + "\\run\\" +matter + "\\" + matter + "_1.FIL", base_path + "\\res\\" +matter + "\\" + matter + "_1.FIL").start();
		process.waitFor();
		process.destroy();
		
		t_info.index[index_thread] = false;
		t_info.current_thread_count--;
		System.out.println("aermod complete(" + matter + ")");
	}
	

	@Override
	public void run() {
			try {
				ReadyProcess();
				Thread.sleep(10);
				RunProcess();
				Thread.sleep(10);
				FinishProcess();
				Thread.sleep(10);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
	}
}

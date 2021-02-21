package aermod;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AERPRE {
	
	String matter;
	Map<String,String> inpparam;
	String base_path;
	
	public AERPRE(String matter, Map<String,String> inpparam, String base_path) {
		this.matter = matter;
		this.inpparam = inpparam;
		this.base_path = base_path;
	}
	
	public void CreateInp() {
		try {
			int ch;
			Reader inStream = new FileReader(base_path + "\\aermod_.inp");
			BufferedWriter outStream = new BufferedWriter(new FileWriter(base_path + "\\run\\aermod_" + matter + ".inp"));
			StringBuilder str = new StringBuilder();
			
			while (true) {
				ch = inStream.read();
				if (ch != ' ' && ch != 10 && ch != 13 && ch != -1) { //  단어를 구분하는 문자가 아닌 경우
					str.append((char) ch);
				} else {
					if (str.length() != 0) {
						if (str.toString().contains("@@!1")) {
							String str2 = str.toString();
							str2 = str2.replace("@@!1", inpparam.get("@@!1"));
							outStream.write(str2, 0, str2.length());
							str = new StringBuilder();
							continue;
						} else if (str.toString().contains("@@!2")) {
							String str2 = str.toString();
							str2 = str2.replace("@@!2", inpparam.get("@@!2"));
							outStream.write(str2, 0, str2.length());
							str = new StringBuilder();
							continue;
						} else if (str.toString().contains("@@!3")) {
							String str2 = str.toString();
							str2 = str2.replace("@@!3", inpparam.get("@@!3"));
							outStream.write(str2, 0, str2.length());
							str = new StringBuilder();
							continue;
						} else if (str.toString().contains("@@!4")) {
							String str2 = str.toString();
							str2 = str2.replace("@@!4", inpparam.get("@@!4"));
							outStream.write(str2, 0, str2.length());
							str = new StringBuilder();
							continue;
						}
						outStream.write(str.toString(), 0, str.toString().length());
						str = new StringBuilder();
					}
					if (ch == -1) {
						System.out.println("complete create INP file(" + matter + ")");
						break;
					}
					outStream.write(String.valueOf((char)ch), 0, String.valueOf((char)ch).length());
				}
				
			}
			inStream.close();
			outStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void CreateSource() {
		
		try {
			ArrayList<String> stack_header = new ArrayList<>();
			ArrayList<Map<String, Double>> stack_info = new ArrayList<>();
			int ch;
			int series1 = 0, series2 = 0; // series : 열의 개수(그 이상은 읽지 않음)
			InputStreamReader inStream = new InputStreamReader(new FileInputStream(base_path + "\\111.csv"),"euc-kr");
//			BufferedWriter outStream = new BufferedWriter(new FileWriter(base_path + "\\run\\aermod_" + matter + ".inp"));
			StringBuilder str = new StringBuilder();
			
			while (true) {
				ch = inStream.read();
				if (ch != ' ' && ch != 10 && ch != 13 && ch != -1 && ch != 44) { //  단어를 구분하는 문자가 아닌 경우
					str.append((char)ch);
				} else {
					if(ch == 44) {
						series1++;
						if(series2 == 0) stack_header.add(str.toString()); // 처음 한줄을 스택 정보 종류를 읽어서 저장
						else if(series2 != 0) {
							String number = str.toString();
							if(number.length() == 0) number = "0";
							stack_info.get(series2-1).put(stack_header.get(series1-1), Double.parseDouble(number));
						}
						// 다음부터는 스택의 정보를 저장, 그에 맞는 이름은 위에서 읽은 스택 정보 종류와 연결시킴
					}
					if (series1 == 35 || (series1 == 34 && ch == 13)) {
						series1 = 0;
						series2++;
						stack_info.add(new HashMap<String, Double>());
					}
					if (str.length() != 0) {
//						outStream.write(str.toString(), 0, str.toString().length());
						str = new StringBuilder();
					}
					if (ch == -1) {
//						System.out.println("complete create INP file(" + matter + ")");
						break;
					}
//					outStream.write(String.valueOf((char)ch), 0, String.valueOf((char)ch).length());
				}
				
			}
			inStream.close();
			stack_info.remove(series2-1);
			
			for(String temp_str : stack_header) {
				System.out.print(temp_str + " ");
			}
			System.out.println("");
			for(Map<String,Double> temp_map : stack_info) {
				for(String temp_str : stack_header) {
					System.out.print(temp_map.get(temp_str) + " ");
				}
				System.out.println("");
			}
			
//			outStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	public void RunProcess() {
		CreateInp();
	}

}

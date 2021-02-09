package calpuff;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JLabel;

public class POSTProcess implements Runnable {

	private Data data;
	private JLabel content;
	private JButton complete;
	private String outputText;
	private JButton back;
	
	public POSTProcess(Data data, JLabel content, JButton complete, JButton back) {
		this.data = data;
		this.content = content;
		this.complete = complete;
		this.back = back;
	}
	
	public void exet() {
		System.out.print("CALPOST 진행....");
		outputText = "<html>--- CALPOST 진행 중 ---<br/>";
		content.setText(outputText);
		String insrc = data.getLoad_path();
		String root = insrc.substring(0, insrc.lastIndexOf("\\"));
		System.out.println("root : " + root);
		try {
			Reader inStream = new FileReader(insrc);
			int ch, series1 = 0, hourNum = 0, reNum = 0, totalhour = 0, datalen = 0, orderNum = 0;
			String polutid = "";
			StringBuilder str = new StringBuilder();
			Double[] xcordinate;
			Double[] ycordinate;
			String[] date;
			Double[][] conc;
			while (true) {
				ch = inStream.read();
				if (ch != ' ' && ch != 10 && ch != 13) {
					str.append((char) ch);
				} else {
					if (str.length() != 0) {
						series1++;
						if (series1 == 4)
							polutid = str.toString();
						if (series1 == 6)
							hourNum = Integer.parseInt(str.toString());
						if (series1 == 15)
							reNum = Integer.parseInt(str.toString());
						if (str.toString().equals("Type:"))
							break;
						str = new StringBuilder();
					}
				}
			}
			// ---------------------------------------------------------------------------------------------------------------------------------

			// 데이터 추출
			// -------------------------------------------------------------------------------------------------------------------------
			series1 = 0;
			totalhour = data.getHourT();
			orderNum = data.getRankT();
			datalen = totalhour / hourNum; //보통은 1시간으로 나눔
			if (orderNum > datalen || orderNum == 0) {
				System.out.println("에러 : 순위가 데이터 개수 범위를 벗어났거나, 0순위를 입력하였습니다.  ( orderNum = " + orderNum + "/ datalen = " + datalen + " )");
				outputText += "에러 : 순위가 데이터 개수 범위를 벗어났거나, 0순위를 입력하였습니다.  ( orderNum = " + orderNum + "/ datalen = " + datalen + " )<br/>";
				content.setText(outputText);
				inStream.close();
				back.setVisible(true);
				return;
			}
			System.out.println("입력 총 시간 : " + totalhour);
			outputText += "입력 총 시간 : " + totalhour +"<br/>";
			content.setText(outputText);
			System.out.println(hourNum + "시간 평균 데이터");
			outputText += hourNum + "시간 평균 데이터" +"<br/>";
			content.setText(outputText);
			System.out.println(reNum + "개의 수용점이 존재합니다.");
			outputText += reNum + "개의 수용점이 존재합니다." +"<br/>";
			content.setText(outputText);
			xcordinate = new Double[reNum];
			ycordinate = new Double[reNum];
			date = new String[datalen];
			conc = new Double[datalen][reNum];
			int series2 = 0, series3 = 0;
			for (int i = 0; i < datalen; i++) {
				date[i] = "";
			}
			System.out.println("진행중....");
			outputText += "진행중....<br/>";
			content.setText(outputText);
			while (true) {
				ch = inStream.read(); //한 글자씩 읽음
				if (ch != ' ' && ch != 10 && ch != 13 && ch != -1) { //  단어를 구분하는 문자가 아닌 경우
					str.append((char) ch);
				} else {
					if (str.length() != 0) {
						series1++;
						if (str.toString().equals("x(km):")) { // 처음 x 좌표를 읽음 y가 나올때까지 x 좌표의 개수를 셈
							series1 = 0;
							series2++; // 0 -> 1
							str = new StringBuilder();
							continue;
						} else if (str.toString().equals("y(km):")) { // y 좌표가 시작되면 읽은 x 좌표의 개수와  수용점 개수를 비교
							if (series1 - 1 != reNum) {
								System.out.println("에러 : 데이터가 충분하지 않습니다(x 좌표 개수 부족)");
								outputText += "에러 : 데이터가 충분하지 않습니다(x 좌표 개수 부족)<br/>";
								content.setText(outputText);
							}
							System.out.println("x data 개수 :" + (series1 - 1));
							series1 = 0;
							series2++; // 1 -> 2
							str = new StringBuilder();
							continue;
						} else if (str.toString().equals("YYYY")) { // 처음 데이터가 시작되는 부분 두 번째 비교(y 좌표 개수 비교)
							if (series1 - 1 != reNum) {
								System.out.println("에러 : 데이터가 충분하지 않습니다(y 좌표 개수 부족)");
								outputText += "에러 : 데이터가 충분하지 않습니다(y 좌표 개수 부족)<br/>";
								content.setText(outputText);
							}
							System.out.println("y data 개수 :" + (series1 - 1));
							series1 = 0;
							series2++; // 2 -> 3
							str = new StringBuilder();
							continue;
						} else if (str.toString().equals("JDY")) {
							series1 = 0;
							str = new StringBuilder();
							continue;
						} else if (str.toString().equals("HHMM")) {
							series1 = 0;
							str = new StringBuilder();
							continue;
						}

						switch (series2) { //데이터 분리
						case 1: {
							xcordinate[series1 - 1] = Double.parseDouble(str.toString());
							break;
						}
						case 2: {
							ycordinate[series1 - 1] = Double.parseDouble(str.toString());
							break;
						}
						case 3: {
							if (series1 == 1 || series1 == 2 || series1 == 3) { // 처음 날짜를 저장
								date[series3] += str.toString() + " "; //하나의 문자열로 합침
							} else {
								conc[series3][series1 - 4] = Double.parseDouble(str.toString()); // 농도 데이터는 Double로 변환하여 저장
							}
							if (series1 - 3 == reNum) { // 날짜 데이터가 3개 추가되므로 3을 빼줌(하루가 지나감)
								series3++;
								series1 = 0;
							}
							break;
						}
						}
						str = new StringBuilder();
					}
				}
				if (ch == -1) {
					System.out.println("읽기 종료");
					outputText += "읽기 종료<br/>";
					content.setText(outputText);
					break;
				}
			}
			if (series3 != datalen) {
				System.out.println("에러 : 데이터가 충분하지 않습니다.(현재 데이터 시간 : " + series3 + ")");
				outputText += "에러 : 데이터가 충분하지 않습니다.(현재 데이터 시간 : " + series3 + ")<br/>";
				content.setText(outputText);
				inStream.close();
				back.setVisible(true);
				return;
			}
			inStream.close();
			// ---------------------------------------------------------------------------------------------------------------------------------

			// 데이터 요약
			// -------------------------------------------------------------------------------------------------------------------------
			// orderArray : 수용점 별 입력 순위 데이터 위치
			int[] orderArray = new int[datalen];
			Double[] gridData = new Double[reNum]; // 그리드 출력시 필요
			Double[] gridAvgData = new Double[reNum];
			String tempstr;
			orderArray = findOrder(conc, datalen, orderNum, reNum);
			// String listsrc =
			// ".\\output\\SUMARRY_"+polutid+"_"+hourNum+"HOUR"+"_"+orderNum+"TH_CONC.DAT";
			String listsrc = root + "\\SUMARRY_" + polutid + "_" + hourNum + "HOUR" + "_" + orderNum + "TH_CONC.DAT";
			BufferedWriter outStream = new BufferedWriter(new FileWriter(listsrc));
			tempstr = "   X       Y      CONC";
			outStream.write(tempstr, 0, tempstr.length());
			outStream.newLine();
			tempstr = "_______ _______ _______";
			outStream.write(tempstr, 0, tempstr.length());
			outStream.newLine();
			for (int i = 0; i < reNum; i++) {
				gridData[i] = conc[orderArray[i]][i];
				tempstr = xcordinate[i] + "	" + ycordinate[i] + "	" + String.format("%.6E", conc[orderArray[i]][i]);
				outStream.write(tempstr, 0, tempstr.length());
				outStream.newLine();
			}
			outStream.close();
			if (hourNum == 1) {
				// String anlistsrc =
				// ".\\output\\SUMARRY_"+polutid+"_"+totalhour+"HOUR"+"_1ST_CONC.DAT";
				String anlistsrc = root + "\\SUMARRY_" + polutid + "_" + totalhour + "HOUR" + "_1ST_CONC.DAT";
				gridAvgData = avgCalc(conc, datalen, reNum);
				BufferedWriter outStream2 = new BufferedWriter(new FileWriter(anlistsrc));
				tempstr = "   X       Y      CONC";
				outStream2.write(tempstr, 0, tempstr.length());
				outStream2.newLine();
				tempstr = "_______ _______ _______";
				outStream2.write(tempstr, 0, tempstr.length());
				outStream2.newLine();
				for (int i = 0; i < reNum; i++) {
					tempstr = xcordinate[i] + "	" + ycordinate[i] + "	" + String.format("%.6E", gridAvgData[i]);
					outStream2.write(tempstr, 0, tempstr.length());
					outStream2.newLine();
				}
				outStream2.close();
			}
			// ---------------------------------------------------------------------------------------------------------------------------------

			// 그리드 데이터 출력 여부 확인
			// ---------------------------------------------------------------------------------------------------------------
			int answer;

			if (data.getGridR()) {
				answer = 1;
			} else {
				answer = 0;
			}

			if (answer == 1) {
				if(Math.sqrt(reNum) % 1 > 0) {
					System.out.println("경고 : 입력 수용점의 개수가 제곱수이어야 합니다.");
					outputText += "경고 : 입력 수용점의 개수가 제곱수이어야 합니다.<br/>";
					content.setText(outputText);
					back.setVisible(true);
					return;
				}
				String gridsrc =  root + "\\GRID_" + polutid + "_" + hourNum + "HOUR" + "_" + orderNum + "TH_CONC.GRD";
				BufferedWriter outStream3 = new BufferedWriter(new FileWriter(gridsrc));
				tempstr = "DSAA";
				outStream3.write(tempstr, 0, tempstr.length());
				outStream3.newLine();
				tempstr = "          " + String.format("%.0f", Math.sqrt(reNum)) + "          "
						+ String.format("%.0f", Math.sqrt(reNum));
				outStream3.write(tempstr, 0, tempstr.length());
				outStream3.newLine();
				tempstr = "     " + String.format("%.3f", findMin(xcordinate)) + "     "
						+ String.format("%.3f", findMax(xcordinate));
				outStream3.write(tempstr, 0, tempstr.length());
				outStream3.newLine();
				tempstr = "    " + String.format("%.3f", findMin(ycordinate)) + "    "
						+ String.format("%.3f", findMax(ycordinate));
				outStream3.write(tempstr, 0, tempstr.length());
				outStream3.newLine();
				tempstr = "  " + String.format("%.4E", findMin(gridData)) + "  "
						+ String.format("%.4E", findMax(gridData));
				outStream3.write(tempstr, 0, tempstr.length());
				outStream3.newLine();
				for (int i = 0; i < Math.sqrt(reNum); i++) {
					tempstr = "";
					for (int j = 0; j < Math.sqrt(reNum); j++) {
						tempstr += String.format("%.4E", gridData[i * (int) Math.sqrt(reNum) + j]) + "	";
					}
					outStream3.write(tempstr, 0, tempstr.length());
					outStream3.newLine();
				}
				outStream3.close();
				if (hourNum == 1) {
					String angridsrc =  root + "\\GRID_" + polutid + "_" + totalhour + "HOUR" + "_1ST_CONC.GRD";
					BufferedWriter outStream4 = new BufferedWriter(new FileWriter(angridsrc));
					tempstr = "DSAA";
					outStream4.write(tempstr, 0, tempstr.length());
					outStream4.newLine();
					tempstr = "          " + String.format("%.0f", Math.sqrt(reNum)) + "          "
							+ String.format("%.0f", Math.sqrt(reNum));
					outStream4.write(tempstr, 0, tempstr.length());
					outStream4.newLine();
					tempstr = "     " + String.format("%.3f", findMin(xcordinate)) + "     "
							+ String.format("%.3f", findMax(xcordinate));
					outStream4.write(tempstr, 0, tempstr.length());
					outStream4.newLine();
					tempstr = "    " + String.format("%.3f", findMin(ycordinate)) + "    "
							+ String.format("%.3f", findMax(ycordinate));
					outStream4.write(tempstr, 0, tempstr.length());
					outStream4.newLine();
					tempstr = "  " + String.format("%.4E", findMin(gridAvgData)) + "  "
							+ String.format("%.4E", findMax(gridAvgData));
					outStream4.write(tempstr, 0, tempstr.length());
					outStream4.newLine();
					for (int i = 0; i < Math.sqrt(reNum); i++) {
						tempstr = "";
						for (int j = 0; j < Math.sqrt(reNum); j++) {
							tempstr += String.format("%.4E", gridAvgData[i * (int) Math.sqrt(reNum) + j]) + "	";
						}
						outStream4.write(tempstr, 0, tempstr.length());
						outStream4.newLine();
					}
					outStream4.close();
				}
				System.out.println("작업을 성공적으로 완료하였습니다.");
				outputText += "작업을 성공적으로 완료하였습니다.<br/>";
				content.setText(outputText);
			} else {
				System.out.println("작업을 성공적으로 완료하였습니다.");
				outputText += "작업을 성공적으로 완료하였습니다.<br/>";
				content.setText(outputText);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		outputText += "완료<br/></html>";
		content.setText(outputText);
		complete.setVisible(true);
	}
	
	int[] findOrder(Double[][] conc, int datalen, int orderNum, int reNum) {
		int[] orderArray = new int[reNum];
		Double[] concorder = new Double[orderNum];
		int[] order = new int[orderNum];

		for (int o = 0; o < reNum; o++) {
			for (int i = 0; i < orderNum; i++) {
				concorder[i] = 0.0;
			}
			for (int i = 0; i < datalen; i++) {
				for (int j = 0; j < orderNum; j++) {
					if (concorder[j] < conc[i][o]) {
						for (int k = orderNum - 1; k > j; k--) {
							concorder[k] = concorder[k - 1];
							order[k] = order[k - 1];
						}
						concorder[j] = conc[i][o];
						order[j] = i;
						break;
					}
				}
			}
			orderArray[o] = order[orderNum - 1];
		}

		return orderArray;
	}

	Double[] avgCalc(Double[][] conc, int datalen, int reNum) {
		Double[] avgconc = new Double[reNum];
		for (int j = 0; j < reNum; j++) {
			avgconc[j] = 0.0;
			for (int i = 0; i < datalen; i++) {
				avgconc[j] += conc[i][j];
			}
			avgconc[j] = avgconc[j] / datalen;
		}
		return avgconc;
	}

	double findMin(Double[] array) {
		double min = array[0];
		for (int i = 1; i < array.length; i++) {
			if (min > array[i]) {
				min = array[i];
			}
		}
		return min;
	}

	double findMax(Double[] array) {
		double max = array[0];
		for (int i = 1; i < array.length; i++) {
			if (max < array[i]) {
				max = array[i];
			}
		}
		return max;
	}

	@Override
	public void run() {
		this.exet();
	}
}

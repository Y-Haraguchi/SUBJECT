package jp.co.iccom.haraguchi_yugi.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

//メイン処理
public class Calculate_Sales_Main {
	public static void main(String[] args) {
		System.out.println(args[0]);
		System.out.println(args[1]);
		try{
			//---------------------------------------------------------------------------------
			//支店定義ファイルの読み込み及び保持
			//---------------------------------------------------------------------------------
			//ファイルパスを指定
			File branchFile = new File(args[0]);

			//読み込み処理
			FileReader branchFr = new FileReader(branchFile);
			BufferedReader blanchBr = new BufferedReader(branchFr);

			//エラーチェック→ディレクトリを見に行った際に「branch.lst」がなかったら処理を終了させる
			if(!branchFile.exists()){
				System.out.println("支店定義ファイルが存在しません");
				blanchBr.close();
				return;
			}

			//支店定義ファイルを読み込んでMapに値を保持
			HashMap<String,String> branchMap = new HashMap<String,String>();

			//一行ずつ読み込むための一時格納変数
			String branchLine;

			/*繰り返しの処理で一行ずつ読み込み、，を基準にsplitで文字分割を行い、
			* splitで保持されたデータをHashMapのキーと値に設定する
			* 読込先がnullなったらループを抜ける処理
			*/

			while((branchLine = blanchBr.readLine()) != null){
				String[] branchstr = branchLine.split(",");
				/*エラーチェック→ファイルフォーマットが不正か
				*正規表現を用いてチェックし不正だったら処理を終了させる
				*/
				if(!(branchstr.length == 2) || !(branchstr[0].matches("^\\d{3}$"))){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					blanchBr.close();
					System.out.println(branchstr[0].matches("\\d{3}$"));
					System.out.println(branchstr.length);
					return;
				}
				branchMap.put(branchstr[0], branchstr[1]);
			}
			//デバッグ出力
			System.out.println("支店定義ファイルが正常に読み込まれました");
			blanchBr.close();
			//デバッグ出力
			System.out.println(branchMap.get("001"));
			System.out.println(branchMap.get("002"));
			System.out.println(branchMap.get("003"));
			System.out.println(branchMap.get("004"));

			System.out.println("==================================================");

			//---------------------------------------------------------------------------------
			//商品定義ファイルの読み込み及び保持
			//---------------------------------------------------------------------------------

			//ファイルパスを指定
			File commodFile = new File(args[1]);

			//エラーチェック③→ディレクトリを見に行った際に「commodity.lst」がなかったら処理を終了させる
			if(!commodFile.exists()){
				System.out.println("商品定義ファイルが存在しません");
				return;
			}
			//読み込み処理
			FileReader commodFr = new FileReader(commodFile);
			BufferedReader commodBr = new BufferedReader(commodFr);

			//支店定義ファイルを読み込んでMapに値を保持
			HashMap<String,String> commodMap = new HashMap<String,String>();

			//一行ずつ読み込むための一時格納変数
			String commodLine;

			/*繰り返しの処理で一行ずつ読み込み、，を基準にsplitで文字分割を行い、
			* splitで保持されたデータをHashMapのキーと値に設定する
			* 読込先がnullなったらループを抜ける処理
			*/

			while((commodLine = commodBr.readLine()) != null){
				String[] commodstr = commodLine.split(",");

				/*エラーチェック→ファイルフォーマットが不正か
				*正規表現を用いてチェックし不正だったら処理を終了させる
				*/
				if(!(commodstr.length == 2) || !(commodstr[0].matches("^[0-9].*[a-zA-Z]|[a-zA-Z].*[0-9].\\d{3}$"))){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					commodBr.close();
					return;
				}
				commodMap.put(commodstr[0], commodstr[1]);
			}
			//デバッグ出力
			System.out.println("商品定義ファイルが正常に読み込まれました");
			commodBr.close();
			//デバッグ出力
			System.out.println(commodMap.get("SFT00001"));
			System.out.println(commodMap.get("SFT00002"));
			System.out.println(commodMap.get("SFT00003"));
			System.out.println(commodMap.get("SFT00004"));
			System.out.println("==================================================");

			//---------------------------------------------------------------------------------
			//売上ファイルの読み込み及び保持、計算を行う
			//---------------------------------------------------------------------------------

			//for文で連番ファイルを読み込む
			//フォルダのパスを指定
			File dir = new File(args[2]);
			//フォルダ内のファイル名取得→「00000001.rcd」の部分
			String[] fileList = dir.list();

			//ファイル内のデータを格納するためのArrayListを生成
			ArrayList<String> salesList = new ArrayList<String>();

			//一行ずつ読み込むための一時格納変数
			String salesLine;

			//繰り替えして
			for(int i = 0 ; i < fileList.length ; i++){
				//「.rcd」の拡張子の場合のみif文の中を実行
				if(fileList[i].matches("^.*.rcd.*$")){
					//連番チェックの為、「.」で文字列を分割する
					String[] divstr = fileList[i].split("\\.");
					//連番チェックの為、divstr[0]番に入っている分割した文字列の数字をint型に変換
					int fileNameNum = Integer.parseInt(divstr[0]);
					//if文で連番チェック→fileNameNumとカウンターの「i」の差が「1」以外は処理を終了させる
					if(!((fileNameNum - i) == 1)){
						System.out.println("ファイルが連番になっていません");
						return;
					}
					//デバッグ表示
					System.out.println("divstr[0]　=" + divstr[0]);
				}
				//ファイルパス指定して1ファイルずつ読み込み
				File salesFile = new File(args[2],fileList[i]);
				//読み込み範囲拡張しが「.rcd」のファイルだけ読み込み
				if(!fileList[i].matches("^.*lst.*$")){
					//売上ファイルの読み込み処理
					FileReader salesFr = new FileReader(salesFile);
					BufferedReader salesBr = new BufferedReader(salesFr);

					while((salesLine = salesBr.readLine()) != null){
						//ArrayListに追加処理
						salesList.add(salesLine);
						//

					}
					//salesBrをクローズ
					salesBr.close();

					//デバッグ表示
					System.out.println(salesFile);
					System.out.println("=============================================");
				}
			}
			//デバッグ表示
			System.out.println(salesList.get(0));
			System.out.println(salesList.get(1));
			System.out.println(salesList.get(2));
			System.out.println(salesList.get(3));
			System.out.println(salesList.get(4));
			System.out.println(salesList.get(5));
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}

}

package jp.co.iccom.haraguchi_yugi.calculate_sales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
			File file1 = new File(args[0]);

			//読み込み処理
			FileReader fr1 = new FileReader(file1);
			BufferedReader br1 = new BufferedReader(fr1);

			//エラーチェック①→ディレクトリを見に行った際に「branch.lst」がなかったら処理を終了させる
			if(!file1.exists()){
				System.out.println("支店定義ファイルが存在しません");
				br1.close();
				return;
			}

			//支店定義ファイルを読み込んでMapに値を保持
			HashMap<String,String> branchMap = new HashMap<String,String>();

			/*繰り返しの処理で一行ずつ読み込み、，を基準にsplitで文字分割を行い、
			* splitで保持されたデータをHashMapのキーと値に設定する
			* 読込先がnullなったらループを抜ける処理
			*/
			String line1;

			while((line1 = br1.readLine()) != null){
				String[] branchstr = line1.split(",");
				//branchstr[0].matches(regex)

				/*エラーチェック②→ファイルフォーマットが不正か
				*正規表現を用いてチェックし不正だったら処理を終了させる
				*/
				if(!(branchstr.length == 2) || !(branchstr[0].matches("^\\d{3}$"))){
					System.out.println("ファイルフォーマットが不正です");
					br1.close();
					System.out.println(branchstr[0].matches("\\d{3}$"));
					System.out.println(branchstr.length);
					return;
				}
				branchMap.put(branchstr[0], branchstr[1]);
			}
			br1.close();
			//デバッグ出力
			//System.out.println(str[0]);
			//System.out.println(str[1]);
			//System.out.println(line);
			System.out.println(branchMap.get("001"));
			System.out.println(branchMap.get("002"));
			System.out.println(branchMap.get("003"));
			System.out.println(branchMap.get("004"));

			System.out.println("==================================================");

			//---------------------------------------------------------------------------------
			//商品定義ファイルの読み込み及び保持
			//---------------------------------------------------------------------------------

			//ファイルパスを指定
			File file2 = new File(args[1]);

			//エラーチェック③→ディレクトリを見に行った際に「commodity.lst」がなかったら処理を終了させる
			if(!file2.exists()){
				System.out.println("商品定義ファイルが存在しません");
				return;
			}
			//読み込み処理
			FileReader fr2 = new FileReader(file2);
			BufferedReader br2 = new BufferedReader(fr2);

			//支店定義ファイルを読み込んでMapに値を保持
			HashMap<String,String> commodMap = new HashMap<String,String>();

			/*繰り返しの処理で一行ずつ読み込み、，を基準にsplitで文字分割を行い、
			* splitで保持されたデータをHashMapのキーと値に設定する
			* 読込先がnullなったらループを抜ける処理
			*/
			String line2;
			while((line2 = br2.readLine()) != null){
				String[] commodstr = line2.split(",");

				/*エラーチェック④→ファイルフォーマットが不正か
				*正規表現を用いてチェックし不正だったら処理を終了させる
				*/
				if(!(commodstr.length == 2) || !(commodstr[0].matches("^[0-9].*[a-zA-Z]|[a-zA-Z].*[0-9].\\d{3}$"))){
					System.out.println("ファイルフォーマットが不正です");
					br2.close();
					System.out.println(commodstr[0].matches("\\d{3}$"));
					System.out.println(commodstr.length);
					return;
				}
				commodMap.put(commodstr[0], commodstr[1]);
			}
			br2.close();
			//デバッグ出力
			//System.out.println(str[0]);
			//System.out.println(str[1]);
			//System.out.println(line);
			System.out.println(commodMap.get("SFT00001"));
			System.out.println(commodMap.get("SFT00002"));
			System.out.println(commodMap.get("SFT00003"));
			System.out.println(commodMap.get("SFT00004"));
			System.out.println("==================================================");
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}



	}

}

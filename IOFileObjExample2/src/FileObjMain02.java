import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class FileObjMain02 {

	public static void main(String[] args) {
		try{
			//ファイルパスを指定
			FileInputStream fs = new FileInputStream("C:\\java\\triangle.ser");

			//FileInputStreamオブジェクト渡すオブジェクトを作成
			ObjectInputStream os = new ObjectInputStream(fs);

			//Triangleオブジェクトを出力
			Triangle tri = (Triangle)os.readObject();
			//ストリームを閉じる
			os.close();

			System.out.println("（" + tri.p0.x + " , " + tri.p0.y + "）");
			System.out.println("（" + tri.p1.x + " , " + tri.p1.y + "）");
			System.out.println("（" + tri.p2.x + " , " + tri.p2.y + "）");
		}
		catch(IOException e){
			System.out.println(e);

		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
	}

}

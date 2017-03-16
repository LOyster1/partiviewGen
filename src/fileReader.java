import java.io.File;

public class fileReader {
	public static void importFolder()
	{
		File folder = new File("individualGEXFs");
		File[] listOfFiles = folder.listFiles();
	
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile())
			{
				System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory())
			{
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
	}
	public static void main(String[] args)
	{
		importFolder();
	}
}

package root;

public class OSDetails {

    public OSDetails(){}

    public static String getOperatingSystemName(){
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")){return "windows";}
        else if (os.contains("nix") || os.contains("nux")){return "linux";}
        else if (os.contains("mac")){return "mac";}
        else return "other";
    }

    public static String getOperationBitSize(){
        return System.getProperty("sun.arch.data.model");
    }

}

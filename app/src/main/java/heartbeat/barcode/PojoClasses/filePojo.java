package heartbeat.barcode.PojoClasses;

/**
 * Created by Heartbeat on 22-06-2017.
 */

public class filePojo {
    private String filename;

    public String getFilename() {
        return filename;
    }



    public filePojo(String filename) {
        this.filename = filename;

    }

    @Override
    public String toString() {
        return "filePojo{" +
                "filename='" + filename + '\'' +
                 +
                '}';
    }
}

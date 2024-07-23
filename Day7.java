import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class FileItem {
    String fileName;
    long fileSize;

    public FileItem(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return this.fileName;
    }

    public long getFileSize() {
        return this.fileSize;
    }
}

class Folder {

    String folderName;
    String address;
    String parentFolderName;
    ArrayList<FileItem> files;
    ArrayList<Folder> childrenFolders;

    public Folder(String folderName, String parentFolderName, String address) {
        this.folderName = folderName;
        this.parentFolderName = parentFolderName;
        this.address = address;
        this.files = new ArrayList<FileItem>();
        this.childrenFolders = new ArrayList<Folder>();
    }

    public String getFolderName() {
        return this.folderName;
    }

    public String getParentFolderName() {
        return this.parentFolderName;
    }

    public String getAddress() {
        return this.address;
    }

    public String getURI() {
        return this.address + this.folderName;
    }

    public ArrayList<FileItem> getFiles() {
        return this.files;
    }

    public ArrayList<Folder> getChildrenFolders() {
        return this.childrenFolders;
    }

    public void addFile(FileItem file) {
        this.files.add(file);
    }

    public void addChildFolder(Folder folder) {
        this.childrenFolders.add(folder);
    }

    public void printcontent() {
        System.out.printf("Folder: %s contains: \n", this.getURI());
        if (this.files.size() != 0) {
            for (FileItem file : this.files) {
                System.out.printf("File: %s with size: %s \n", file.getFileName(), file.getFileSize());
            }
        }
        if (this.childrenFolders.size() != 0) {
            for (Folder folder : this.childrenFolders) {
                System.out.printf("Folder: %s \n", folder.getFolderName());
            }
        }
    }

    public long getSize() {
        long sum = 0;

        if (this.files.size() != 0) {
            for (FileItem file : this.files) {
                sum += file.getFileSize();
            }
        }

        if (this.childrenFolders.size() != 0) {
            for (Folder folder : this.childrenFolders) {
                sum += folder.getSize();
            }
        }
        return sum;
    }

    public void printSize() {
        System.out.printf("Folder: %s has size: %s \n", this.getURI(), this.getSize());
    }

}

class Day7 {
    public static void main(String[] args) {
        try {
            File inputFile = new File("./assets/day7-input.txt");
            Scanner fileScanner = new Scanner(inputFile);

            ArrayList<Folder> allFolders = new ArrayList<Folder>();
            String currentDirectory = "";

            readInput(fileScanner, allFolders, currentDirectory);

            for (Folder folder : allFolders) {
                if (folder.getFolderName().equals("/")) {
                    long size = folder.getSize();
                    System.out.printf("The size of the root folder is: %s \n", String.valueOf(size));
                }
            }

            fileScanner.close();

        } catch (FileNotFoundException error) {
            System.out.println("An error occurred.");
            error.printStackTrace();
        }
    }

    private static void readInput(Scanner fileScanner, ArrayList<Folder> allFolders, String currentDirectory) {
        while (fileScanner.hasNextLine()) {
            String str = fileScanner.nextLine();
            String[] tokens = str.split(" ");
            if (tokens[0].equals("$")) {
                if (tokens[1].equals("cd")) {
                    if (tokens[2].equals("/")) {
                        currentDirectory = "/";
                        Folder parent = new Folder(currentDirectory, "", "");
                        allFolders.add(parent);
                    } else if (tokens[2].equals("..")) {
                        currentDirectory = changeCurrentDirectoryToParent(currentDirectory);
                    } else {
                        if (currentDirectory.equals("/")) {
                            currentDirectory += tokens[2];
                        } else {
                            currentDirectory = currentDirectory + "/" + tokens[2];
                        }
                    }
                } else if (tokens[1].equals("ls")) {

                }

            } else if (tokens[0].equals("dir")) {
                String folderName = tokens[1];
                Folder newFolder = new Folder(folderName, getParentNameFrom(currentDirectory),
                        currentDirectory);
                for (Folder folder : allFolders) {
                    if (folder.getAddress().equals(getParentAddressFrom(currentDirectory)) &&
                            folder.getFolderName().equals(getParentNameFrom(currentDirectory))) {
                        folder.addChildFolder(newFolder);
                    }
                }
                allFolders.add(newFolder);
            } else if (isNumeric(tokens[0])) {
                FileItem newFile = new FileItem(tokens[1], Long.parseLong(tokens[0]));
                for (Folder folder : allFolders) {
                    if (folder.getAddress().equals(getParentAddressFrom(currentDirectory)) &&
                            folder.getFolderName().equals(getParentNameFrom(currentDirectory))) {
                        folder.addFile(newFile);
                    }
                }
            }
        }
    }

    private static String getParentNameFrom(String currentDirectory) {
        String[] tokens = currentDirectory.split("/");
        String parent = "";
        if (tokens.length == 0) {
            parent = "/";
            return parent;
        } else {
            parent = tokens[tokens.length - 1];
        }
        return parent;
    }

    private static String changeCurrentDirectoryToParent(String currentDirectory) {
        String[] tokens = currentDirectory.split("/");
        String parent = "";
        String[] newTokens = Arrays.copyOf(tokens, tokens.length - 1);
        for (int i = 0; i < newTokens.length - 1; i++) {
            parent += newTokens[i] + "/";
        }
        parent += newTokens[newTokens.length - 1];
        return parent;
    }

    private static String getParentAddressFrom(String currentDirectory) {
        String parent = "";
        String[] tokens = currentDirectory.split("/");
        if (tokens.length == 0) {
            return parent;
        }
        List<String> realTokens = new ArrayList<>();

        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                realTokens.add(tokens[i]);
            }
        }
        if (realTokens.size() == 0) {
            return parent;
        } else if (realTokens.size() == 1) {
            return "/";
        } else {
            realTokens.remove(realTokens.size() - 1);
            parent += "/";
            if (realTokens.size() == 1) {
                parent += realTokens.get(0);
                return parent;
            } else {
                for (int i = 0; i < realTokens.size() - 1; i++) {
                    parent += realTokens.get(i) + "/";
                }
                parent += realTokens.get(realTokens.size() - 1);
            }
        }
        return parent;
    }

    private static boolean isNumeric(String string) {
        try {
            @SuppressWarnings("unused")
            long a = Long.parseLong(string);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
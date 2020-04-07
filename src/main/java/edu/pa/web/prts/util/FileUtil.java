package edu.pa.web.prts.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * Provide several file operation methods for ATS.
 *
 * @author QRX QRXwzx@outlook.com
 * @date  2020-02-10
 */
public class FileUtil {

    /** Don't permit user construct this class, as this is a util class. */
    private FileUtil() { }

    public static final String JAVA_SUFFIX = ".java";
    public static final String CLASS_SUFFIX = ".class";
    public static final String TXT_SUFFIX = ".txt";
    public static final String NEW_LINE = System.lineSeparator();

    /**
     *
     * @param line a src line
     * @return flag: whether this line need a \n.
     */
    private static boolean needNewLine(String line) {
        String trimLine = line.trim();
        return
                trimLine.endsWith(";") || trimLine.endsWith("{") || trimLine.endsWith("}") ||
                trimLine.startsWith("@") || trimLine.startsWith("if") || trimLine.startsWith("for");
    }

    /**
     * TODO: add later
     * @param file
     * @throws IOException
     */
    public static void multiLineToOneForJavaFile(File file) throws IOException {
        if(!file.exists()) {
            throw new IllegalArgumentException("File is not exits:" + file.getAbsolutePath());
        }
        if(file.isDirectory()) {
            List<File> allJavas = getAllFilesBySuffix(file, JAVA_SUFFIX);
            for (File javaFile : allJavas) {
                multiLineToOneForJavaFile(javaFile);
            }
            return;
        }
        List<String> lines = readContentsLineByLine(file);
        StringBuilder newContentBuider = new StringBuilder(lines.size() * 150);
        for (String line : lines) {
            newContentBuider.append(line);
            if(needNewLine(line)) {
                newContentBuider.append(NEW_LINE);
            }
        }
        String ouputPath = writeContentIntoFile(file, newContentBuider.toString());
        System.out.println("Change multi-line to one for [" + ouputPath + "] done");
    }

    public static void multiLineToOneForJavaFile(String path) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }
        multiLineToOneForJavaFile(new File(path));
    }

    /**
     * Clear all blank lines for a java file.
     *
     * @param file is a java file.
     * @throws IOException if read wrongly.
     *
     * @date 2020-03-18
     */
    public static void clearBlankLinesForJavaFile(File file) throws IOException {
        if(!file.exists()) {
            throw new IllegalArgumentException("File is not exits:" + file.getAbsolutePath());
        }
        if(file.isDirectory()) {
            List<File> allJavas = getAllFilesBySuffix(file, JAVA_SUFFIX);
            for (File javaFile : allJavas) {
                clearBlankLinesForJavaFile(javaFile);
            }
            return;
        }
        List<String> lines = readContentsLineByLine(file);
        StringBuilder newContentBuilder = new StringBuilder(lines.size() * 150);
        for (String line : lines) {

            if("".equals(line.trim())) {
                continue;
            }
            newContentBuilder.append(line).append(NEW_LINE);
        }
        String outputPath = writeContentIntoFile(file, newContentBuilder.toString());
        System.out.println("Clear blank lines for [" + outputPath + "] done.");
    }

    public static void clearBlankLinesForJavaFile(String path) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }
        clearBlankLinesForJavaFile(new File(path));
    }

    /**
     * Clear all comment for a java file.
     *
     * @param file is a java source file.
     * @param charset file charset, default is UTF-8.
     * @throws IOException if read wrongly.
     *
     * @date 2020-03-18
     */
    public static void clearCommentsForJavaFile(File file, String charset) throws IOException {
        if(!file.exists()) {
            throw new IllegalArgumentException("File is not exits:" + file.getAbsolutePath());
        }
        if(file.isDirectory()) {
            List<File> allJavas = getAllFilesBySuffix(file, JAVA_SUFFIX);
            for (File javaFile : allJavas) {
                clearCommentsForJavaFile(javaFile, charset);
            }
            return;
        }
        String content = readAllcontent(file, charset);
        String regex = "\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*+\\/";
//        String regex = "\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*\\/";
        String newContent = content.replaceAll(regex, "");
        String outputPath = writeContentIntoFile(file, newContent);
        System.out.println("Clear comments for [" + outputPath + "] done.");
    }

    public static void clearCommentsForJavaFile(File file) throws IOException {
        clearCommentsForJavaFile(file, "UTF-8");
    }

    public static void clearCommentsForJavaFile(String path, String charset) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }
        clearCommentsForJavaFile(new File(path), charset);
    }

    public static void clearCommentsForJavaFile(String path) throws IOException {
        clearCommentsForJavaFile(path, "UTF-8");
    }


    /**
     * Read all content from a readable file.
     *
     * @param file is a readable file.
     * @param charset charset of the file.
     * @return content of the file.
     * @throws IOException if read wrongly.
     *
     * @date 2020-03-18
     */
    public static String readAllcontent(File file, String charset) throws IOException {
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid file. Please input a path of file.");
        }
        if(!file.canRead()) {
            throw new IllegalArgumentException(file.getAbsolutePath() + ": cannot be read");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
        StringBuilder contentBuilder = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            contentBuilder.append(line).append(NEW_LINE);
        }
        br.close();
        return contentBuilder.toString();
    }

    /**
     * Read all content from a readable file. Use default charset: UTF-8.
     *
     * @param file is a readable file.
     * @return content of the file.
     * @throws IOException if read wrongly.
     *
     * @date 2020-03-18
     */
    public static String readAllcontent(File file) throws IOException {
        return readAllcontent(file, "UTF-8");
    }

    public static String readAllcontent(String path) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }
        return readAllcontent(new File(path));
    }


    /**
     *
     * @param path of the file you want to get its base name.
     * @return base name of a file.
     *
     * @date 2020-03-18
     */
    public static String fileBaseName(String path) {
        File file = new File(path);
        return file.getName().replace(suffixOf(file), "");
    }

    /**
     * Read content from a txt file, one line for one item.
     *
     * @param file A readable file.
     * @return A List of parsing result.
     *
     * @date 2020-03-18
     */
    public static List<String> readContentsLineByLine(File file) throws IOException {
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid file. Please input a path of file.");
        }
        if(!file.canRead()) {
            throw new IllegalArgumentException(file.getAbsolutePath() + ": cannot be read");
        }

        List<String> contents = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while((line = br.readLine()) != null) {
            contents.add(line);
        }

        br.close();
        return contents;
    }
    /**
     * Read content from a txt file, one line for one item.
     *
     * @param path A path of a property file, written in a txt file.
     * @return A List of parsing result.
     *
     * @date 2020-03-18
     */
    public static List<String> readContentsLineByLine(String path) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }
        File file = new File(path);
        return readContentsLineByLine(file);
    }

    public static String writeContentIntoFile(File file, String content) throws IOException {
        if(!file.exists()) {
            boolean newFile = file.createNewFile();
            if(newFile) {
                System.out.println("Create new file: " + file.getAbsolutePath());
            } else {
                throw new RuntimeException("Create new file failed!");
            }
        }
        if(!file.canWrite()) {
            throw new IllegalArgumentException(file + ": cannot be written");
        }
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid path. Please input file path.");
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(content);
        bw.newLine();

        bw.close();
        return file.getAbsolutePath();
    }


    /**
     *
     * @param path A path of a file.
     * @param content The content needed be written into the file.
     * @return The absolute path of written file.
     * @throws IOException when write wrongly.
     *
     * @date 2020-03-18
     */
    public static String writeContentIntoFile(String path, String content) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }
        File file = new File(path);
        return writeContentIntoFile(file, content);
    }

    public static String writeContentsIntoFile(String path, List<String> contents) throws IOException {
        StringBuilder builder = new StringBuilder(contents.size() * 100);
        contents.forEach((content) -> builder.append(content).append(System.lineSeparator()));
        return writeContentIntoFile(path, builder.toString());
    }



    /**
     * Read contents from a file by line numbers.
     *
     * @param path A path of a file.
     * @param lineNumbers Line number which is corresponding to target contents.
     * @return A list of <code>String</code>s represent target contents
     * @throws IOException Throw IOException when read file wrong
     * @throws IllegalArgumentException When path is null.
     * @throws IllegalArgumentException When path is not a file path
     *
     * @date 2020-02-12
     */
    public static List<String> readContentsByLineNumbers(String path, HashSet<Integer> lineNumbers) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }

        File file = new File(path);
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid path. Please input file path.");
        }
        if(!file.canRead()) {
            throw new IllegalArgumentException(path + ": cannot be read");
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> contents = new ArrayList<>();
        int cnt = 1;
        for(String line = reader.readLine() ; line != null; line = reader.readLine()) {
            if(lineNumbers.contains(cnt)) {
                contents.add(line);
            }
            cnt++;
        }

        reader.close();
        return contents;
    }

    /**
     * Read content from a file by line number.
     *
     * @param path A path of a file.
     * @param lineNumber Line number which is corresponding to target content.
     * @return A String of Target content.
     * @throws IOException Throw IOException when read file wrong
     * @throws IllegalArgumentException When line number is negative.
     * @throws IllegalArgumentException When path is null.
     * @throws IllegalArgumentException When path is not a file path.
     *
     * @date 2020-02-12
     */
    public static String readContentByLineNumber (String path, int lineNumber) throws IOException {
        if(path == null) {
            throw new IllegalArgumentException("Path should not be null.");
        }
        if(lineNumber < 0) {
            throw new IllegalArgumentException("Negative line number.");
        }

        File file = new File(path);
        if(!file.isFile()) {
            throw new IllegalArgumentException("Invalid path. Please input file path.");
        }
        if(!file.canRead()) {
            throw new IllegalArgumentException(path + ": cannot be read");
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        int cnt = 1;
        for(String line = reader.readLine() ; line != null; line = reader.readLine()) {
            if(cnt == lineNumber) {
                reader.close();
                return line;
            }
            cnt++;
        }

        reader.close();
        return "";
    }

    /**
     * Get a suffix of a file.
     *
     * @param file A file.
     * @return A file's <param>file</param> suffix.
     *
     * @date  2020-02-10
     */
    public static String suffixOf(File file) {
        if(file.isDirectory()) {
            System.err.println("Warning: Directory has no explicit suffix!");
        }
        String absolutePath = file.getAbsolutePath();
        int lastDotPos = absolutePath.lastIndexOf('.');
        String suffix = "";
        if(lastDotPos != -1) {
            suffix = absolutePath.substring(lastDotPos);
        }
        return suffix;
    }





    /**
     * Get all files that has a suffix as <param>suffix</param>
     * under directory <param>directory</param> recursively.
     *
     * @param directory A directory.
     * @param suffix The type of target files. suffix should start with a '.'.
     * @return A List of target files.
     * @throws IllegalArgumentException When dir doesn't represent a directory.
     *
     * @date 2020-03-18
     */
    public static List<File> getAllFilesBySuffix(File directory, String suffix) {
        if(!directory.isDirectory()) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + " should be a directory!");
        }
        // Give out warning when suffix doesn't start with '.'.
        if(suffix.charAt(0) != '.') {
            System.err.println(
                    "Warning: suffix \"" + suffix + "\" should start with a \'.\', system have added for you automatically."
            );
            suffix = "." + suffix;
        }
        // Get all files end with suffix recursively.
        List<File> targetFiles = new ArrayList<>();
        File[] allFiles = directory.listFiles();
        if(allFiles != null) {
            // Add files end with the target suffix recursively.
            for (File file : allFiles) {
                if(file.isDirectory()) {
                    targetFiles.addAll(getAllFilesBySuffix(file.getAbsolutePath(), suffix));
                } else {
                    if(suffixOf(file).equals(suffix)) {
                        targetFiles.add(file);
                    }
                }
            }
        }
        return targetFiles;
    }

    /**
     *  Get all files that has a suffix as <param>suffix</param>
     *  under directory <param>dir</param> recursively.
     *
     * @param dirPath An absolute path of a directory.
     * @param suffix The type of target files. suffix should start with a '.'.
     * @return A List of target files.
     * @throws IllegalArgumentException When dir doesn't represent a directory.
     *
     * @date 2020-02-10
     */
    public static List<File> getAllFilesBySuffix(String dirPath, String suffix) {
        File directory = new File(dirPath);
        return getAllFilesBySuffix(directory, suffix);
    }


    @Deprecated
    public static String compileJavaFile(String outputDir, String ... javaPaths) {
        File outputDirectory = new File(outputDir);

        if(!outputDirectory.exists()) {
            System.err.println("Warning: \'" + outputDir + "\' does not exists!");
            boolean mkdirSuccess = outputDirectory.mkdir();
            if(mkdirSuccess) {
                System.err.println("Create output directory " + outputDirectory.getAbsolutePath() + "successfully!" );
            } else {
                System.err.println("Create output directory " + outputDirectory.getAbsolutePath() + "failed!");
            }

        /*  TODO: Different OS may have different cmd operations.
            else {
                throw new IOException("Create output directory failed!");
            }
        */
        }

        String outputPath = outputDirectory.getAbsolutePath();
        // TODO: Different OS may have different cmd operations.
        String CMD = "cmd.exe /c ";
        for (String javaPath : javaPaths) {
            String cmd = CMD + javaPath + " -d" + outputPath;
            System.out.println("cmd: " + cmd);
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return outputPath;
    }
}

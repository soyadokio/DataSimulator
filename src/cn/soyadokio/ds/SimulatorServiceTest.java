package cn.soyadokio.ds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import cn.soyadokio.ds.core.SimulatorService;
import cn.soyadokio.ds.util.FileUtils;

public class SimulatorServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorServiceTest.class);

    public static void main(String[] args) {
        new SimulatorServiceTest();
    }

    SimulatorServiceTest() {
        // 获取主程序运行时所在目录
        URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
        String resourceDir = null;
        try {
            resourceDir = URLDecoder.decode(url.getPath(), "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        }
        if (resourceDir.endsWith(".jar")) {
            resourceDir = resourceDir.substring(1, resourceDir.lastIndexOf('/') + 1);
        }
        logger.debug("取得主程序运行目录: {}", resourceDir);

        // 按照自定义配置文件配置SLF4J
        loadLogbackConfig();

        // 查找/生成数据表描述文件[tableinfo.conf]
        String tableinfoPath = resourceDir + "tableinfo.conf";
        logger.info("在当前目录搜索数据表描述文件[tableinfo.conf]： {}", tableinfoPath);
        File tableinfo = new File(tableinfoPath);
        if (tableinfo.exists()) {
            logger.info("已找到数据表描述文件。");
        } else {
            logger.info("未找到数据表描述文件，将在主程序目录生成模板。");
            // 从JAR中解压出数据表描述文件[tableinfo.conf]
            try {
                decompressResourceFromJar("tableinfo.conf", resourceDir);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            logger.info("成功生成数据表描述文件模板。");
        }

        // 按照配置文件生成数据
        String sqlScript = null;
        try {
            sqlScript = testSimulate(tableinfoPath);
//            sqlScript = testSimulate(tableinfoPath, 5);
            logger.info("成功生成SQL Script数据。");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("未知异常。");
        }

        // 打印至控制台
//        System.out.println(sqlScript);

        // 输出至文件
        try {
            String sqlScriptPath = writeFile(sqlScript);
            logger.info("成功生成SQL Script[.sql]文件： {}。", sqlScriptPath);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            logger.error("成功生成SQL Script[.sql]文件时发生异常。");
        }

    }

    /**
     * Method Name: loadLogbackConfig
     * Description: 读取自定义的SLF4J的配置文件，并以之配置SLF4J
     * @since		JDK 1.8.0_144
     */
    private void loadLogbackConfig() {
        File logbackConfig = new File("logback.xml");
        if (logbackConfig.exists()) {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            try {
                configurator.doConfigure(logbackConfig);
            } catch (JoranException je) {
                // StatusPrinter will handle this
            }
        }
    }

    /**
     * Method Name: testSimulate
     * Description: 按照指定的配置文件生成SQL Script，生成的数据条数以tableinfo文件中设定的记录条数为准
     * @param tableinfoPath 数据表描述文件的绝对路径
     * @return      生成的SQL Script
     * @throws Exception
     * @since		JDK 1.8.0_144
     */
    public String testSimulate(String tableinfoPath) throws Exception {
        SimulatorService service = new SimulatorService(tableinfoPath);
        String sqlScript = service.simulate();
        return sqlScript;
    }

    /**
     * Method Name: testSimulate
     * Description: 按照指定的配置文件、数据条数生成SQL Script
     * @param rows  指定的数据条数，该条数会覆盖tableinfo文件中设定的记录条数
     * @param tableinfoPath 数据表描述文件的绝对路径
     * @return      生成的SQL Script
     * @throws Exception
     * @since		JDK 1.8.0_144
     */
    public String testSimulate(String tableinfoPath, int rows) throws Exception {
        SimulatorService service = new SimulatorService(tableinfoPath);
        String sqlScript = service.simulate(rows);
        return sqlScript;
    }

    /**
     * Method Name: decompressResourceFromJar
     * Description: 从Jar包中释放资源文件（包括Bin、Text）
     * @param resourceName  表示所需释放资源文件的文件名
     * @param decompressDir 表示将资源文件释放后存放的位置，结尾须是“/”
     * @throws IOException
     * @since		JDK 1.8.0_144
     */
    private void decompressResourceFromJar(String resourceName, String decompressDir) throws IOException {
        // If the file does not exist yet, it will be created. If the file exists already, it will be ignored
        File file = new File(decompressDir + resourceName);

        if (!file.createNewFile()) {
            logger.warn("create file :{} failed, it's already exist.", decompressDir + resourceName);
        }

        // Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;

        // Open and check input stream
        InputStream is = getClass().getClassLoader().getResource(resourceName).openStream();
        if (is == null) {
            throw new FileNotFoundException("File " + resourceName + " was not found in JAR.");
        }

        // Open output stream and copy data between source file in JAR and the temporary file
        OutputStream os = new FileOutputStream(file);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
            os.flush();
        } finally {
            // If read/write fails, close streams safely before throwing an exception
            os.close();
            is.close();
        }
    }

    /**
     * Method Name: writeFile
     * Description: 将文件写入指定.sql文件
     * @param sql
     * @return      所生成SQL Script文件[.sql]的绝对路径
     * @throws IOException
     * @since		JDK 1.8.0_144
     */
    private String writeFile(String sql) throws IOException {
        String tablename = "default";
        if (sql.startsWith("insert into ") && sql.indexOf("(") != -1) {
            tablename = sql.substring(12, sql.indexOf("("));
        }
        File sqlScript = new File(tablename + ".sql");
        FileUtils.write(sqlScript, sql, "UTF-8");
        return sqlScript.getAbsolutePath();
    }

}
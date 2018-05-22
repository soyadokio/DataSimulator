package cn.soyadokio.ds.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.soyadokio.ds.bean.SimulatorInfo;
import cn.soyadokio.ds.bean.FieldInfo;
import cn.soyadokio.ds.bean.TableInfo;
import cn.soyadokio.ds.util.MyUtils;

public class SimulatorService {

    private static final Logger logger = LoggerFactory.getLogger(SimulatorService.class);

    private String filename;// 数据库表描述文件-文件名
    private SimulatorInfo simulatorInfo = new SimulatorInfo();

    public SimulatorService(String filename) {
        this.filename = filename;
        init();
    }

    private void init() {
        BufferedReader reader = null;
        try {
            List<TableInfo> tableInfoList = new ArrayList<>();
            reader = new BufferedReader(new FileReader(this.filename));
            String line;
            TableInfo tableInfo = null;
            List<FieldInfo> fieldInfoList = null;
            while ((line = reader.readLine()) != null) {
                if (MyUtils.isNullOrEmpty(line)) {
                    continue;
                }
                if (line.trim().startsWith("//") || line.trim().startsWith("#")) {
                    continue;
                }
                if (MyUtils.startsWithIgnoreCase(line, 0, "table")) {
                    if (tableInfo != null) {
                        tableInfo.fieldInfos = fieldInfoList;
                        tableInfoList.add(tableInfo);
                    }
                    tableInfo = new TableInfo();
                    fieldInfoList = new ArrayList<>();
                    String[] strs = line.split(":");
                    if (strs.length != 2) {
                        throw new Exception("table name line is error, line:" + line);
                    } else {
                        String[] nameAndLine = strs[1].split(" ");
                        tableInfo.setTablename(nameAndLine[0].trim());
                        if (nameAndLine.length == 2) {
                            tableInfo.rows = MyUtils.toInt(nameAndLine[1].trim());
                        }
                    }
                } else {
                    FieldInfo fieldInfo = FieldInfo.of(line);
                    if (fieldInfoList == null) {
                        logger.info("请先输入表名，再输入字段信息。");
                        throw new Exception("表名信息缺失。");
                    } else {
                        fieldInfoList.add(fieldInfo);
                    }
                }
            }
            if (tableInfo != null) {
                tableInfo.fieldInfos = fieldInfoList;
                tableInfoList.add(tableInfo);
            }
            simulatorInfo.tableInfos = tableInfoList;

        } catch (FileNotFoundException ex) {
            logger.info("FileNotFoundException, ex:{}", ex);
        } catch (IOException ex) {
            logger.info("IOException, ex:{}", ex);
        } catch (Exception ex) {
            logger.info("Exception, ex:{}", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String simulate() throws Exception {
        return simulatorInfo.produce();
    }

    public String simulate(int rows) throws Exception {
        return simulatorInfo.produce(rows);
    }

}

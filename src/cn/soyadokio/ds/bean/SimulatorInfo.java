package cn.soyadokio.ds.bean;

import java.util.List;

public class SimulatorInfo {

    public List<TableInfo> tableInfos;

    public String produce() throws Exception {
        StringBuffer sb = new StringBuffer();
        for (TableInfo tableInfo : tableInfos) {
            sb.append(tableInfo.produce());
            sb.append("\n");
        }
        return sb.toString();
    }

    public String produce(int rows) throws Exception {
        StringBuffer sb = new StringBuffer();
        for (TableInfo tableInfo : tableInfos) {
            sb.append(tableInfo.produce(rows));
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "EmulateInfo{" +
                "tableInfos=" + tableInfos +
                '}';
    }

}

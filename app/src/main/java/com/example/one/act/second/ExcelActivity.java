package com.example.one.act.second;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.IBackgroundFormat;
import com.bin.david.form.data.format.draw.IDrawFormat;
import com.bin.david.form.data.format.title.ITitleDrawFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.style.LineStyle;
import com.bin.david.form.data.table.ArrayTableData;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.matrix.MatrixHelper;
import com.bin.david.form.utils.DensityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;
import com.example.one.bean.ExcelBean;
import com.example.one.utils.ExcelUtils;
import com.example.one.utils.RootUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelActivity extends AppCompatActivity {

    SmartTable smartTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel);
        Button button = (Button) findViewById(R.id.btExcelStart);
        button.setOnClickListener(v -> {
            try {
                File file = new File("/sdcard/One/test.xlsx");
                ExcelUtils.readExcel(file);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        });
        init();
        RootUtils.execShellCmd("adb tcpip 5556");
    }

    private void init(){
        smartTable =(SmartTable) findViewById(R.id.rvExcel);
        TableConfig tableConfig = smartTable.getConfig();



        Column<String> column1 = new Column<String>("1","name");
        Column<String> column2 = new Column<String>("1","year");
        List<ExcelBean> list = new ArrayList<>();
        ExcelBean bean = new ExcelBean();
        bean.setName("万一");
        bean.setYear("发发发");
        ExcelBean bean1 = new ExcelBean();
        bean1.setName("万一");
        bean1.setYear("fffffffffffffffffffffffffffffffffffff");
        list.add(bean);
        list.add(bean);
        list.add(bean1);
        list.add(bean);



        TableData<ExcelBean> tableData = new TableData<>("",list,column1,column2);
        column1.setWidth(DensityUtils.dp2px(ExcelActivity.this,5));
        column1.setMinWidth(DensityUtils.dp2px(ExcelActivity.this,5));
        LogUtils.e(column1.getTextAlign());

        tableConfig.setHorizontalPadding(0);
        tableConfig.setVerticalPadding(0);
//        List<Column> columns = new ArrayList<>();
//        columns.add(column1);
//        columns.add(column2);
//
//        ITitleDrawFormat iTitleDrawFormat = new ITitleDrawFormat() {
//            @Override
//            public int measureWidth(Column column, TableConfig config) {
//                return DensityUtils.dp2px(ExcelActivity.this,200);
//            }
//
//            @Override
//            public int measureHeight(TableConfig config) {
//                return DensityUtils.dp2px(ExcelActivity.this,50);
//            }
//
//            @Override
//            public void draw(Canvas c, Column column, Rect rect, TableConfig config) {
//
//            }
//        };
//        TableData<ExcelBean> tableData = new TableData<ExcelBean>("",list,columns,iTitleDrawFormat);

        column1.setAutoMerge(true);
        column2.setAutoMerge(true);
        smartTable.setTableData(tableData);

        column2.setWidth(100);

        column1.setTextAlign(Paint.Align.CENTER);

        tableConfig.setShowTableTitle(false);
        tableConfig.setShowXSequence(false);
        tableConfig.setShowYSequence(false);

        tableConfig.setShowColumnTitle(false);


        tableConfig.setContentStyle(new FontStyle(20,Color.BLACK));
        LineStyle lineStyle2 = new LineStyle();
        lineStyle2.setColor(Color.argb(255,200,227,252));
        tableConfig.setContentGridStyle(lineStyle2);



    }
}
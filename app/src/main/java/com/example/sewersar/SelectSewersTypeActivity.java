package com.example.sewersar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sewersar.database.SewersNode;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;

public class SelectSewersTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sewers_type);

        ConstraintLayout constraintLayout = findViewById(R.id.rootContainer);
        TextView tv = findViewById(R.id.selectSewerTextView);

        ConstraintSet set = new ConstraintSet();

        SewersNodesController.getAllSewersTypesStrings().clear();
        SewersNodesController.getSelectedSewersTypesStrings().clear();

        for (int i = 0; i < SewersNodesController.getSewersNodes().size(); i++) {
            String typeName = SewersNodesController.getSewersNodes().get(i).type;
            if(!SewersNodesController.getAllSewersTypesStrings().contains(typeName)) {
                SewersNodesController.getAllSewersTypesStrings().add(typeName);
            }
        }

        for (int i = 0; i < SewersNodesController.getAllSewersTypesStrings().size(); i++) {
            addCheckbox(SewersNodesController.getAllSewersTypesStrings().get(i), 200 + i*100, constraintLayout, set, tv);
        }



    }

    private void addCheckbox(String name, int margin, ConstraintLayout constraintLayout, ConstraintSet set, TextView tv) {
        CheckBox cb = new CheckBox(this);
        cb.setId(View.generateViewId());
        cb.setText(name);
        constraintLayout.addView(cb);

        cb.setOnClickListener(view -> {
            if(((CompoundButton) view).isChecked()){
                SewersNodesController.getSelectedSewersTypesStrings().add(cb.getText().toString());
            } else {
                int removedTypeIndex = SewersNodesController.getSelectedSewersTypesStrings().indexOf(cb.getText().toString());
                SewersNodesController.getSelectedSewersTypesStrings().remove(removedTypeIndex);
            }
        });

        set.clone(constraintLayout);
        set.connect(cb.getId(), ConstraintSet.TOP, tv.getId(), ConstraintSet.TOP, margin);
        set.connect(cb.getId(),ConstraintSet.LEFT,constraintLayout.getId(),ConstraintSet.LEFT);
        set.connect(cb.getId(),ConstraintSet.RIGHT,constraintLayout.getId(),ConstraintSet.RIGHT);
        set.applyTo(constraintLayout);
    }

    public void acceptSelectedSewersTypes(View view) {
        SewersNodesController.setSelectedSewersChanged(true);

        this.finish();
    }


}

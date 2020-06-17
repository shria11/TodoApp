package com.example.todomvvm.addedittask;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.todomvvm.R;
import com.example.todomvvm.database.TaskEntry;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEditFragment extends Fragment {
    EditText mEditText, mEditText2;
    RadioGroup mRadioGroup;
    Button mButton;

    AddEditTaskViewModel viewModel;

    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;

    View rootview;

    public AddEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.fragment_add_edit, container, false);
        initViews();


        if (AddEditTaskViewModel.mTaskID == AddEditTaskViewModel.DEFAULT_TASK_ID) {
            // populate the UI


            AddEditTaskViewModelFactory factory = new AddEditTaskViewModelFactory(getActivity().getApplication(), AddEditTaskViewModel.mTaskID);
            viewModel = ViewModelProviders.of(this, factory).get(AddEditTaskViewModel.class);

            viewModel.getTask().observe(getActivity(), new Observer<TaskEntry>() {
                @Override
                public void onChanged(TaskEntry taskEntry) {
                    viewModel.getTask().removeObserver(this);
                }
            });

        } else {
            AddEditTaskViewModelFactory factory = new AddEditTaskViewModelFactory(getActivity().getApplication(), AddEditTaskViewModel.mTaskID);
            viewModel = ViewModelProviders.of(this, factory).get(AddEditTaskViewModel.class);
        }
        return rootview;
    }

    public void onSaveButtonClicked() {
        // Not yet implemented
        String description = mEditText.getText().toString();
        int priority = getPriorityFromViews();
        Date date = new Date();
        String location = mEditText2.getText().toString();
        TaskEntry todo = new TaskEntry(description, priority, date, location);
        if (AddEditTaskViewModel.mTaskID == AddEditTaskViewModel.DEFAULT_TASK_ID)
            viewModel.insertTask(todo);
        else {
            todo.setId(AddEditTaskViewModel.mTaskID);
            viewModel.updateTask(todo);

        }

    }

    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) rootview.findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }

    private void initViews() {
        mEditText = rootview.findViewById(R.id.editTextTaskDescription);
        mEditText2 = rootview.findViewById(R.id.editTextTaskLocation);
        mRadioGroup = rootview.findViewById(R.id.radioGroup);

        mButton = rootview.findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) rootview.findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) rootview.findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) rootview.findViewById(R.id.radioGroup)).check(R.id.radButton3);
        }
    }

    private void populateUI(TaskEntry task) {
        if (task == null) {
            return;
        }
        mEditText.setText(task.getDescription());
        mEditText2.setText(task.getLocation());
        setPriorityInViews(task.getPriority());

    }

    public void openLocation(View view) {
        mEditText2 = rootview.findViewById(R.id.editTextTaskLocation);
        // Get the string indicating a location. Input is not validated; it is
        // passed to the location handler intact.
        String loc = mEditText2.getText().toString();

        // Parse the location and create the intent.
        Uri addressUri = Uri.parse("geo:0,0?q=" + loc);
        Intent intent = new Intent(Intent.ACTION_VIEW, addressUri);

        // Find an activity to handle the intent, and start that activity.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("ImplicitIntents", "Can't handle this intent!");
        }
    }

}

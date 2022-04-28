package com.example.wakeapp.ui.alarms;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wakeapp.MainActivity;
import com.example.wakeapp.R;
import com.example.wakeapp.SettingsActivity;
import com.example.wakeapp.databinding.FragmentAlarmsBinding;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class AlarmsFragment extends Fragment implements OnToggleAlarmListener{

    private AlarmRecyclerViewAdapter alarmRecyclerViewAdapter;
    private AlarmViewHolder alarmViewHolder;
    private AlarmsViewModel alarmsViewModel;
    private RecyclerView alarmsRecyclerView;
    private Button addAlarm;
    private Button deleteAlarm;
    private FragmentAlarmsBinding binding;
    private AlarmRepository alarmRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alarmRecyclerViewAdapter = new AlarmRecyclerViewAdapter(this);
        alarmsViewModel = new ViewModelProvider(this).get(AlarmsViewModel.class);
        alarmsViewModel.getAlarmsLiveData().observe(this, new Observer<List<Alarm>>() {
            @Override
            public void onChanged(List<Alarm> alarms) {
                if (alarms != null) {
                    alarmRecyclerViewAdapter.setAlarms(alarms);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_alarms, container, false);

        alarmsViewModel =
                new ViewModelProvider(this).get(AlarmsViewModel.class);

        binding = FragmentAlarmsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        alarmsRecyclerView = root.findViewById(R.id.fragment_listalarms_recylerView);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmsRecyclerView.setAdapter(alarmRecyclerViewAdapter);

        return root;
    }

    @Override
    public void onToggle(Alarm alarm) {
        if (alarm.isStarted()) {
            alarm.cancelAlarm(getContext());
            alarmsViewModel.update(alarm);
        } else {
            alarm.schedule(getContext());
            alarmsViewModel.update(alarm);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --------------AddAlarm button-------------------------
        addAlarm = view.findViewById(R.id.fragment_listalarms_addAlarm);
        addAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateAlarmActivity.class);
                startActivity(intent);
            }
        });

        // --------------------Initialization of the swipe gesture to delete an alarm------------------------------
        ItemTouchHelper itemTouchHelperCallback = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int i) {

                // --------AlertDialog to warn the user about the risk to delete an Alarm definitively-----------
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure you want to delete this alarm?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int newPos = viewHolder.getAdapterPosition();
                                Alarm alarm = alarmRecyclerViewAdapter.getAlarmAtPosition(newPos);
                                //Toast.makeText(getContext(), "Alarm deleted", Toast.LENGTH_SHORT).show();
                                alarm.cancelAlarm(getContext());
                                alarmsViewModel.update(alarm);
                                alarmsViewModel.deleteAlarm(alarm);
                                alarmRecyclerViewAdapter.notifyItemRemoved(newPos);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alarmRecyclerViewAdapter.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        // To attach the ItemTouchHelper to the Recyclerview
        itemTouchHelperCallback.attachToRecyclerView(alarmsRecyclerView);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

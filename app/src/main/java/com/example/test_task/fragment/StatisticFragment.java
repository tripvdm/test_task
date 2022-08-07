package com.example.test_task.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.test_task.R;
import com.example.test_task.model.Statistic;
import com.example.test_task.presenter.StatisticPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatisticFragment extends Fragment implements StatisticPresenter.StatisticView {
    public static final String TAG = StatisticFragment.class.getSimpleName();

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.countRecords)
    TextView countOfRecords;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.timeOfFirstRecord)
    TextView timeOfFirstRecord;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.timeOfLastRecord)
    TextView timeOfLastRecord;

    private Context context;
    private StatisticPresenter statisticPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressLint({"NewApi", "UseRequireInsteadOfGet"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        ButterKnife.bind(this, view);
        context = getContext();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statisticPresenter = new StatisticPresenter(context);
        statisticPresenter.attachStatisticsView(this);
        if (savedInstanceState == null) {
            statisticPresenter.findOfStatistic();
        } else {
            Statistic statistic = (Statistic) savedInstanceState.get("statistic");
            displayStatistic(statistic);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("statistic", statisticPresenter.getStatistic());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        statisticPresenter.findOfStatistic().dispose();
        statisticPresenter.detachStatisticsView();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void displayStatistic(Statistic statistic) {
        String delimiter = " ";
        countOfRecords.setText(getString(R.string.countOfRecords) + delimiter + statistic.getCountOfRecords());
        timeOfFirstRecord.setText(getString(R.string.dateOfFirstRecord) + delimiter + statistic.getDateOfFirstRecord());
        timeOfLastRecord.setText(getString(R.string.dateOfLastRecord) + delimiter + statistic.getDateOfLastRecord());
    }
}

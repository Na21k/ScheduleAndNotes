package com.na21k.schedulenotes.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.na21k.schedulenotes.databinding.ScheduleFragmentBinding;

public class ScheduleFragment extends Fragment {

    private ScheduleViewModel mViewModel;
    private ScheduleFragmentBinding binding;

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        binding = ScheduleFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //TODO: observe vars in the vm and display vm data in the binding

        return root;
    }
}

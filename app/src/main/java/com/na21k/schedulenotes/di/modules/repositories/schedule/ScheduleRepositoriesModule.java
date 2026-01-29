package com.na21k.schedulenotes.di.modules.repositories.schedule;

import com.na21k.schedulenotes.data.database.Schedule.Event;
import com.na21k.schedulenotes.repositories.CanClearRepository;
import com.na21k.schedulenotes.repositories.CanSearchRepository;
import com.na21k.schedulenotes.repositories.MutableRepository;
import com.na21k.schedulenotes.repositories.Repository;
import com.na21k.schedulenotes.repositories.schedule.ScheduleRepository;
import com.na21k.schedulenotes.repositories.schedule.ScheduleRepositoryImpl;

import dagger.Binds;
import dagger.Module;

@Module(includes = EventNotificationAlarmPendingIntentRepositoriesModule.class)
public interface ScheduleRepositoriesModule {

    @Binds
    Repository<Event> bindEventsRepository(ScheduleRepositoryImpl scheduleRepository);

    @Binds
    MutableRepository<Event> bindEventsRepositoryMutable(ScheduleRepositoryImpl scheduleRepository);

    @Binds
    ScheduleRepository bindScheduleRepository(ScheduleRepositoryImpl scheduleRepository);

    @Binds
    CanSearchRepository<Event> bindCanSearchScheduleRepository(ScheduleRepositoryImpl scheduleRepository);

    @Binds
    CanClearRepository<Event> bindCanClearScheduleRepository(ScheduleRepositoryImpl scheduleRepository);
}

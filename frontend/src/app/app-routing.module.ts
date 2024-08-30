import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RoomsComponent} from './rooms/rooms.component';
import {TimetableComponent} from './timetable/timetable.component';
import {ConfirmationComponent} from './confirmation/confirmation.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {StudentGroupComponent} from './student-group/student-group.component';
import {ConstraintsComponent} from './constraints/constraints.component';
import {TimeslotsComponent} from './timeslots/timeslots.component';
import {TeachersComponent} from './teachers/teachers.component';
import {LessonsComponent} from './lessons/lessons.component';


const routes: Routes = [
  {
    path: 'dashboard',
    component: DashboardComponent
  },
  {
    path: 'rooms',
    component: RoomsComponent
  },
  {
    path: 'timeslots',
    component: TimeslotsComponent
  },
  {
    path: 'teachers',
    component: TeachersComponent
  },
  {
    path: 'studentGroups',
    component: StudentGroupComponent
  },
  {
    path: 'lessons',
    component: LessonsComponent
  },
  {
    path: 'constraints',
    component: ConstraintsComponent
  },
  {
    path: 'confirmation',
    component: ConfirmationComponent
  },
  {
    path: 'timetable',
    component: TimetableComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {
}

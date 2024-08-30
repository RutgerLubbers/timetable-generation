import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {interval, Subscription} from 'rxjs';

@Component({
  selector: 'app-countdown-timer',
  templateUrl: './countdown-timer.component.html',
  styleUrls: ['./countdown-timer.component.css'],
})
export class CountdownTimerComponent implements OnInit, OnDestroy {
  @Input() duration: number | undefined = 0; // Problem duration in minutes

  hours: string = "00";
  minutes: string = "00";
  seconds: string = "00";

  private countdownSubscription: Subscription | undefined;

  constructor() {
  }

  ngOnInit(): void {
    this.startCountdown();
  }

  ngOnDestroy(): void {
    if (this.countdownSubscription) {
      this.countdownSubscription.unsubscribe();
    }
  }

  private startCountdown(): void {
    if (this.duration) {
      const countdownInterval = interval(1000); // Update every second
      var totalSeconds = Math.max(0, this.duration * 60); // Convert duration to seconds

      this.countdownSubscription = countdownInterval.subscribe(() => {
        if (totalSeconds <= 0) {
          this.countdownSubscription?.unsubscribe(); // Stop the countdown
        } else {
          this.hours = String(Math.floor(totalSeconds / 3600)).padStart(2, '0');
          this.minutes = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, '0');
          this.seconds = String(totalSeconds % 60).padStart(2, '0');
          totalSeconds--;
        }
      });
    }
  }
}

import { Component, OnInit, Input, Output } from '@angular/core';

@Component({
  selector: 'profile-text-input',
  templateUrl: './text-input.component.html',
  styleUrls: ['./text-input.component.scss']
})
export class TextInputComponent implements OnInit {
  @Input() width: number;
  @Input() placeholderText: string;
  @Input() readonly: boolean;
  @Input() value: string;

  constructor() { }

  ngOnInit() {}

}

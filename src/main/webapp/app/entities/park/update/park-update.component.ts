import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ParkFormService, ParkFormGroup } from './park-form.service';
import { IPark } from '../park.model';
import { ParkService } from '../service/park.service';

@Component({
  selector: 'jhi-park-update',
  templateUrl: './park-update.component.html',
})
export class ParkUpdateComponent implements OnInit {
  isSaving = false;
  park: IPark | null = null;

  editForm: ParkFormGroup = this.parkFormService.createParkFormGroup();

  constructor(protected parkService: ParkService, protected parkFormService: ParkFormService, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ park }) => {
      this.park = park;
      if (park) {
        this.updateForm(park);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const park = this.parkFormService.getPark(this.editForm);
    if (park.id !== null) {
      this.subscribeToSaveResponse(this.parkService.update(park));
    } else {
      this.subscribeToSaveResponse(this.parkService.create(park));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPark>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(park: IPark): void {
    this.park = park;
    this.parkFormService.resetForm(this.editForm, park);
  }
}

import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { EquipementFormService, EquipementFormGroup } from './equipement-form.service';
import { IEquipement } from '../equipement.model';
import { EquipementService } from '../service/equipement.service';
import { IPark } from 'app/entities/park/park.model';
import { ParkService } from 'app/entities/park/service/park.service';

@Component({
  selector: 'jhi-equipement-update',
  templateUrl: './equipement-update.component.html',
})
export class EquipementUpdateComponent implements OnInit {
  isSaving = false;
  equipement: IEquipement | null = null;

  parksSharedCollection: IPark[] = [];

  editForm: EquipementFormGroup = this.equipementFormService.createEquipementFormGroup();

  constructor(
    protected equipementService: EquipementService,
    protected equipementFormService: EquipementFormService,
    protected parkService: ParkService,
    protected activatedRoute: ActivatedRoute
  ) {}

  comparePark = (o1: IPark | null, o2: IPark | null): boolean => this.parkService.comparePark(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ equipement }) => {
      this.equipement = equipement;
      if (equipement) {
        this.updateForm(equipement);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const equipement = this.equipementFormService.getEquipement(this.editForm);
    if (equipement.id !== null) {
      this.subscribeToSaveResponse(this.equipementService.update(equipement));
    } else {
      this.subscribeToSaveResponse(this.equipementService.create(equipement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEquipement>>): void {
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

  protected updateForm(equipement: IEquipement): void {
    this.equipement = equipement;
    this.equipementFormService.resetForm(this.editForm, equipement);

    this.parksSharedCollection = this.parkService.addParkToCollectionIfMissing<IPark>(this.parksSharedCollection, equipement.park);
  }

  protected loadRelationshipsOptions(): void {
    this.parkService
      .query()
      .pipe(map((res: HttpResponse<IPark[]>) => res.body ?? []))
      .pipe(map((parks: IPark[]) => this.parkService.addParkToCollectionIfMissing<IPark>(parks, this.equipement?.park)))
      .subscribe((parks: IPark[]) => (this.parksSharedCollection = parks));
  }
}

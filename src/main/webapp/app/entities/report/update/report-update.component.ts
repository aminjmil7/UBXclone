import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ReportFormService, ReportFormGroup } from './report-form.service';
import { IReport } from '../report.model';
import { ReportService } from '../service/report.service';
import { IEquipement } from 'app/entities/equipement/equipement.model';
import { EquipementService } from 'app/entities/equipement/service/equipement.service';
import { IPark } from 'app/entities/park/park.model';
import { ParkService } from 'app/entities/park/service/park.service';

@Component({
  selector: 'jhi-report-update',
  templateUrl: './report-update.component.html',
})
export class ReportUpdateComponent implements OnInit {
  isSaving = false;
  report: IReport | null = null;

  equipementsSharedCollection: IEquipement[] = [];
  parksSharedCollection: IPark[] = [];

  editForm: ReportFormGroup = this.reportFormService.createReportFormGroup();

  constructor(
    protected reportService: ReportService,
    protected reportFormService: ReportFormService,
    protected equipementService: EquipementService,
    protected parkService: ParkService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareEquipement = (o1: IEquipement | null, o2: IEquipement | null): boolean => this.equipementService.compareEquipement(o1, o2);

  comparePark = (o1: IPark | null, o2: IPark | null): boolean => this.parkService.comparePark(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ report }) => {
      this.report = report;
      if (report) {
        this.updateForm(report);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const report = this.reportFormService.getReport(this.editForm);
    if (report.id !== null) {
      this.subscribeToSaveResponse(this.reportService.update(report));
    } else {
      this.subscribeToSaveResponse(this.reportService.create(report));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IReport>>): void {
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

  protected updateForm(report: IReport): void {
    this.report = report;
    this.reportFormService.resetForm(this.editForm, report);

    this.equipementsSharedCollection = this.equipementService.addEquipementToCollectionIfMissing<IEquipement>(
      this.equipementsSharedCollection,
      report.equipement
    );
    this.parksSharedCollection = this.parkService.addParkToCollectionIfMissing<IPark>(this.parksSharedCollection, report.park);
  }

  protected loadRelationshipsOptions(): void {
    this.equipementService
      .query()
      .pipe(map((res: HttpResponse<IEquipement[]>) => res.body ?? []))
      .pipe(
        map((equipements: IEquipement[]) =>
          this.equipementService.addEquipementToCollectionIfMissing<IEquipement>(equipements, this.report?.equipement)
        )
      )
      .subscribe((equipements: IEquipement[]) => (this.equipementsSharedCollection = equipements));

    this.parkService
      .query()
      .pipe(map((res: HttpResponse<IPark[]>) => res.body ?? []))
      .pipe(map((parks: IPark[]) => this.parkService.addParkToCollectionIfMissing<IPark>(parks, this.report?.park)))
      .subscribe((parks: IPark[]) => (this.parksSharedCollection = parks));
  }
}

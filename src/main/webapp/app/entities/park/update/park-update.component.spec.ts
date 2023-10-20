import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ParkFormService } from './park-form.service';
import { ParkService } from '../service/park.service';
import { IPark } from '../park.model';

import { ParkUpdateComponent } from './park-update.component';

describe('Park Management Update Component', () => {
  let comp: ParkUpdateComponent;
  let fixture: ComponentFixture<ParkUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let parkFormService: ParkFormService;
  let parkService: ParkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ParkUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ParkUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParkUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    parkFormService = TestBed.inject(ParkFormService);
    parkService = TestBed.inject(ParkService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const park: IPark = { id: 456 };

      activatedRoute.data = of({ park });
      comp.ngOnInit();

      expect(comp.park).toEqual(park);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPark>>();
      const park = { id: 123 };
      jest.spyOn(parkFormService, 'getPark').mockReturnValue(park);
      jest.spyOn(parkService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ park });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: park }));
      saveSubject.complete();

      // THEN
      expect(parkFormService.getPark).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(parkService.update).toHaveBeenCalledWith(expect.objectContaining(park));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPark>>();
      const park = { id: 123 };
      jest.spyOn(parkFormService, 'getPark').mockReturnValue({ id: null });
      jest.spyOn(parkService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ park: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: park }));
      saveSubject.complete();

      // THEN
      expect(parkFormService.getPark).toHaveBeenCalled();
      expect(parkService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPark>>();
      const park = { id: 123 };
      jest.spyOn(parkService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ park });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(parkService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});

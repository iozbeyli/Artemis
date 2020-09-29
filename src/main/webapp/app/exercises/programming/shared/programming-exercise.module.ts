import { NgModule } from '@angular/core';
import { ProgrammingExerciseComponent } from 'app/exercises/programming/manage/programming-exercise.component';
import { ArtemisProgrammingExerciseStatusModule } from 'app/exercises/programming/manage/status/programming-exercise-status.module';
import { OrionModule } from 'app/shared/orion/orion.module';
import { ArtemisSharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { ArtemisProgrammingExerciseGradingModule } from 'app/exercises/programming/manage/grading/programming-exercise-grading.module';
import { ProgrammingExerciseUtilsModule } from 'app/exercises/programming/shared/utils/programming-exercise-utils.module';
import { FeatureToggleModule } from 'app/shared/feature-toggle/feature-toggle.module';
import { ProgrammingExerciseRowButtonsComponent } from 'app/exercises/programming/manage/programming-exercise-row-buttons.component';

@NgModule({
    imports: [
        ArtemisSharedModule,
        FeatureToggleModule,
        RouterModule,
        OrionModule,
        ArtemisProgrammingExerciseStatusModule,
        ArtemisProgrammingExerciseGradingModule,
        ProgrammingExerciseUtilsModule,
    ],
    declarations: [ProgrammingExerciseComponent, ProgrammingExerciseRowButtonsComponent],
    exports: [ProgrammingExerciseComponent, ProgrammingExerciseRowButtonsComponent],
})
export class ArtemisProgrammingExerciseModule {}

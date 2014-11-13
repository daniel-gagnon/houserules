var gulp = require('gulp');
var sass = require('gulp-ruby-sass');
var autoprefixer = require('gulp-autoprefixer');

gulp.task('sass', function () {
    return gulp.src('resources/public/scss/*.scss')
        .pipe(sass({sourcemap: true, sourcemapPath: '.'}))
        .on('error', function (err) { console.log(err.message); })
        .pipe(gulp.dest('resources/public/css/'));
});

gulp.task('prefix', ['sass'], function() {
    return gulp.src('resources/public/css/*.css')
            .pipe(autoprefixer({
                browsers: ['ie > 9 last two versions']
            }))
            .pipe(gulp.dest('resources/public/css/'));
});

gulp.task('watch', function() {
    gulp.watch('resources/public/scss/*.scss', ['sass']);
});

gulp.task('default', ['sass', 'prefix'], function() {});

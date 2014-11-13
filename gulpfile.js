var gulp = require('gulp');
var sass = require('gulp-ruby-sass');
var autoprefixer = require('gulp-autoprefixer');
var concat = require('gulp-concat');
var csso = require('gulp-csso');

gulp.task('sass', function () {
    return gulp.src('resources/public/scss/*.scss')
        .pipe(sass())
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

gulp.task('prod', ['prefix'], function() {
    return gulp.src('resources/public/css/semantic.css', 'resources/public/css/styles.css')
        .pipe(concat('styles.min.css'))
        .pipe(csso())
        .pipe(gulp.dest('resources/public/css/'));
});

gulp.task('default', ['sass', 'prefix'], function() {});
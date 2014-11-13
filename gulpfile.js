var gulp = require('gulp');
var sass = require('gulp-ruby-sass');

gulp.task('sass', function () {
    return gulp.src('resources/public/scss/*.scss')
        .pipe(sass({sourcemap: true, sourcemapPath: '.'}))
        .on('error', function (err) { console.log(err.message); })
        .pipe(gulp.dest('resources/public/css/'));
});

gulp.task('default', ['sass'], function() {});
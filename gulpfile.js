var gulp = require('gulp');
var sass = require('gulp-ruby-sass');
var autoprefixer = require('gulp-autoprefixer');
var concat = require('gulp-concat');
var csso = require('gulp-csso');
var exec = require('child_process').exec;

gulp.task('sass', function () {
    return gulp.src('resources/public/scss/*.scss')
        .pipe(sass())
        .on('error', function (err) { console.log(err.message); })
        .pipe(gulp.dest('resources/public/css/'));
});

gulp.task('prefix', ['sass'], function() {
    return gulp.src('resources/public/css/*.css')
            .pipe(autoprefixer({
                browsers: ['ie > 9, last two versions']
            }))
            .pipe(gulp.dest('resources/public/css/'));
});

gulp.task('watch', function() {
    gulp.watch('resources/public/scss/*.scss', ['sass']);
});

gulp.task('minify', ['prefix'], function() {
    return gulp.src('resources/public/css/semantic.css', 'resources/public/css/styles.css')
        .pipe(concat('styles.min.css'))
        .pipe(csso())
        .pipe(gulp.dest('resources/public/css/'));
});


gulp.task('build', ['minify'], function (cb) {
  exec('lein ring uberjar', function (err, stdout, stderr) {
    console.log(stdout);
    console.log(stderr);
    cb(err);
  });
})


gulp.task('default', ['sass', 'prefix'], function() {});
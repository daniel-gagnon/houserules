var gulp = require('gulp');
var sass = require('gulp-ruby-sass');
var autoprefixer = require('gulp-autoprefixer');
var concat = require('gulp-concat');
var csso = require('gulp-csso');
var exec = require('child_process').exec;
var rimraf = require('rimraf');
var argv   = require('yargs').argv;
var gulpif = require('gulp-if');
var insert = require('gulp-insert')

var prod = argv.prod

gulp.task('clean', function() {
    rimraf.sync('./target');
    rimraf.sync('./resources/public/css')
    rimraf.sync('./resources/public/js')
    rimraf.sync('./.sass-cache')
});

gulp.task('sass', function () {
    return gulp.src(['resources/public/sass/*.sass', 'resources/public/sass/*.scss'])
        .pipe(gulpif(function(file) {return prod && /\.sass$/.test(file.path)}, insert.prepend('$prod: true\n')))
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

gulp.task('watch', ['prefix', 'copy-themes'], function(cb) {
    gulp.watch(['resources/public/sass/*.sass', 'resources/public/sass/*.scss'], ['prefix']);
});

gulp.task('minify', ['prefix'], function() {
    return gulp.src(['resources/public/css/semantic.css', 'darkroom.min.css', 'resources/public/css/styles.css'])
        .pipe(concat('styles.min.css'))
        .pipe(csso())
        .pipe(gulp.dest('resources/public/css/'));
});

gulp.task('copy-themes', function() {
    return gulp.src('resources/themes/**')
        .pipe(gulp.dest('resources/public/css/themes/'))
});

gulp.task('build', ['clean', 'minify', 'copy-themes'], function (cb) {
  exec('lein ring uberjar', function (err, stdout, stderr) {
    console.log(stdout);
    console.log(stderr);
    cb(err);
  });
})

gulp.task('default', ['build'], function() {});
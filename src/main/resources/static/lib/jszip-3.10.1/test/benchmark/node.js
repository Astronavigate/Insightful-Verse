"use strict";

globalThis.Benchmark = require("static/lib/jszip-3.10.1/test/benchmark/benchmark");
globalThis.JSZip = require("../../lib");

const benchmark = require("./benchmark");
benchmark("nodebuffer");

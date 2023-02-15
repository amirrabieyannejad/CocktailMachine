#!/usr/bin/env ruby
# -*- encoding: utf-8 -*-

require "optimist"

opts = Optimist::options do
  opt :build,   "build sketch"
  opt :upload,  "upload to board"
  opt :monitor, "monitor serial output"
  opt :verbose, "verbose"
  opt :profile, "board profile", default: "feather"
  opt :port,    "usb port to use", default: "/dev/ttyUSB0"
end

case opts[:profile]
when "feather", "esp"
  puts "using profile: #{opts[:profile]}"
else
  warn "unsupported profile: #{opts[:profile]}"
  exit 1
end

if opts[:build]
  args = [
    "--output-dir", "out",
    "--profile", opts[:profile],
    opts[:verbose] ? "--verbose" : nil,
  ].reject(&:nil?)

  system("arduino-cli", "compile", *args, *ARGV)
end

if opts[:upload]
  args = [
    "--port", opts[:port],
    "--profile", opts[:profile],
    opts[:verbose] ? "--verbose" : nil,
  ].reject(&:nil?)

  system("arduino-cli", "upload", *args, *ARGV)
end

if opts[:monitor]
  args = [
    "--port", opts[:port],
    "--config", "baudrate=115200",
  ].reject(&:nil?)

  system("arduino-cli", "monitor", *args, *ARGV)
end

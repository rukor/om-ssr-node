# Deprecation Notice:

This library is deprecated in favour of [cljs-ssr-node](http://github.com/rukor/cljs-ssr-node), which supports both OM and Reagent out of the box and makes it trivial to support other view technologies.



# om-ssr-node

A tiny library to support OM-based CLJS development development on NodeJS with server side rendering and silk-based route handling.

This started out as om-ssr targeting the JVM but, seemed a bit to heavy for front-end-only apps that should not have much overhead.

## Usage

Add `[com.firstlinq/om-ssr-node "0.1.0"]` to your dependencies and `[lein-npm "0.4.0']` to your plugin dependencies. 

Then type `lein npm pprint > package.json`. This will generate a package.json consisting of node dependencies in this project and your project plus that defined in any other dependencies that you have defined. 


This is a pre-alpha quality proof of concept, pull requests welcome.

See [om-ssr-hello](http://github.com/rukor/om-ssr-hello) for a working sample app, and [om-ssr-app](http://github.com/rukor/om-ssr-app) for a leiningen template that can be 
used to bootrap webapps based on om-ssr-node.

## TODO

- Documentation
- Tests

## License

Copyright © 2015 Roland Ukor

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

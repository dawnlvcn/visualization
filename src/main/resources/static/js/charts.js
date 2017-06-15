function TreeMap(rootContainer) {
    console.log(rootContainer);
    var instance = {};
    var _width, _height, _bodyWidth, _bodyHeight;
    var _margin = {
        top    : 10,
        right  : 10,
        bottom : 10,
        left   : 10
    };
    var _data, _selection;
    var color = d3.scaleOrdinal(d3.schemeCategory20c);

    var init = function(container) {
        _selection = (container instanceof d3.selection) ? container : d3.select(container);
        _width = parseInt((_width = _selection.attr("width")) ? _width : _selection.style("width"));
        _height = parseInt((_height = _selection.attr("height")) ? _height : _selection.style("height"));
        _selection = _selection.append("div")
                               .attr("class", "graph-body")
                               .style("top", _margin.top + "px")
                               .style("left", _margin.left + "px");

    };

    var computeBoby = function() {
        _bodyWidth = (_width - _margin.left - _margin.right);
        _bodyHeight = (_height - _margin.top - _margin.bottom);
    };

    if (rootContainer) {
        init(rootContainer);
    }

    instance.render = function(container) {
        if (container !== undefined) {
            init(container);
        }
        computeBoby();
        console.log(_data);
        const treemap = d3.treemap().size([_bodyWidth, _bodyHeight]);
        const tree = treemap(_data);
        _selection.style("width", _bodyWidth + "px")
                  .style("height", _bodyHeight + "px")
                  .datum(_data)
                  .selectAll(".treeMapNode")
                  .data(tree.leaves())
                  .enter()
                  .append("div")
                  .style("position", "absolute")
                  .attr("class", "treeMapNode")
                  .style("left", function(d) {
                      return d.x0 + "px";
                  })
                  .style("top", function(d) {
                      return d.y0 + "px";
                  })
                  .style("width", function(d) {
                      return Math.max(0, d.x1 - d.x0 - 1) + "px";
                  })
                  .style("height", function(d) {
                      return Math.max(0, d.y1 - d.y0 - 1) + "px";
                  })
                  .style("line-height", function(d) {
                      return Math.max(0, d.y1 - d.y0 - 1) + "px";
                  })
                  .style("vertical-align", "middle")
                  .style("background", function(d) {
                      return color(d.data.name);
                  })
                  .text(function(d) {
                      return d.data.name;
                  })
                  .on("mouseover", function(d) {
                      var currColor = d3.color(color(d.data.name));
                      currColor.opacity = 0.6;
                      d3.select(this)
                        .style("background", d3.rgb(currColor))
                        .style("cursor", "pointer");
                  })
                  .on('mouseout', function(d) {
                      d3.select(this)
                        .style("background", d3.rgb(color(d.data.name)))
                        .style("cursor", "pointer");
                  });
        return instance;
    };

    instance.data = function(data, keyFunction, sumFunction) {
        if (!arguments.length) {
            return _data;
        }
        console.log(data);
        _data = d3.hierarchy(data, keyFunction ? keyFunction : function(d) {
            return d.children;
        }).sum(sumFunction ? sumFunction : function(d) {
            return d.weight ? 1 : 0;
        });
        console.log(_data);
        return instance;
    };

    instance.call = function(callBack) {
        _selection.selectAll(".treeMapNode").call(callBack);
        return instance;
    };

    instance.height = function(h) {
        if (!arguments.length) {
            return _height;
        }
        _height = h;
        return instance;
    };

    instance.width = function(w) {
        if (!arguments.length) {
            return _width;
        }
        _width = w;
        return instance;
    };

    instance.margin = function(position, value) {
        if (position !== undefined) {
            position = position.toLowerCase();
        }
        if (arguments.length == 2) {
            if (_margin[position]) {
                _margin[position] = value;
            }
        } else if (arguments.length == 1) {
            return _margin[position];
        }

        return instance;
    }

    return instance;
}

function Force(rootContainer) {
    console.log(rootContainer);
    var instance = {};
    var _width, _height, _bodyWidth, _bodyHeight;
    var _margin = {
        top    : 10,
        right  : 10,
        bottom : 10,
        left   : 10
    };
    var _data, _selection;
    var color = d3.scaleOrdinal(d3.schemeCategory20c);
    var ridus = d3.scaleLinear().domain([1, 10]).range([20, 40]);
    var simulation, links, nodes, label;

    var init = function(container) {
        _selection = (container instanceof d3.selection) ? container : d3.select(container);
        _selection.selectAll("*").remove();
        _width = parseInt((_width = _selection.attr("width")) ? _width : _selection.style("width"));
        _height = parseInt((_height = _selection.attr("height")) ? _height : _selection.style("height"));
        _selection = _selection.append("svg")
                               .attr("width", _width)
                               .attr("height", _height)
                               .append("g")
                               .attr("class", "graphSvg")
                               .style("top", _margin.top + "px")
                               .style("left", _margin.left + "px");

    };

    var computeBoby = function() {
        _bodyWidth = (_width - _margin.left - _margin.right);
        _bodyHeight = (_height - _margin.top - _margin.bottom);
    };

    if (rootContainer) {
        init(rootContainer);
    }

    instance.render = function(container) {

        if (container !== undefined) {
            init(container);
        }
        computeBoby();
        console.log(_data);

        var graphSvgZoom = d3.zoom()
                             .scaleExtent([1, Infinity])
                             .translateExtent([[0, 0], [_bodyWidth, _bodyHeight]])
                             .extent([[0, 0], [_bodyWidth, _bodyHeight]])
                             .on("zoom", function() {
                                 _selection.attr("transform", d3.event.transform);
                             });

        _selection.attr("width", _bodyWidth)
                  .attr("height", _bodyHeight)
                  .call(graphSvgZoom);

        _selection.on("click", function() {
            _selection.transition()
                      .duration(750)
                      .call(graphSvgZoom.transform, d3.zoomIdentity);
        });

        _selection.append("rect")
                  .attr("class", "background")
                  .attr("width", _bodyWidth)
                  .attr("height", _bodyHeight)
                  .style("left", _margin.left + "px")
                  .style("top", _margin.top + "px");

        simulation = d3.forceSimulation()
                       .force("link", d3.forceLink().id(function(d) {
                           return d.name;
                       }).distance(function distance() {
                           return 30;
                       }))
                       .force("collide", d3.forceCollide(80).iterations(5))
                       .force("charge", d3.forceManyBody()
                                          .strength(function strength() {
                                              return -40;
                                          }))
                       .force("center", d3.forceCenter(width / 2, height / 2));
        links = _selection.append("g")
                          .attr("class", "force-links")
                          .selectAll("line")
                          .data(_data.links)
                          .enter()
                          .append("line")
                          .attr("stroke-width", function(d) {
                              return Math.sqrt(d.level);
                          })
                          .attr("stroke", "black");

        nodes = _selection.append("g")
                          .attr("class", "force-nodes")
                          .selectAll("circle")
                          .data(_data.nodes)
                          .enter()
                          .append("circle")
                          .attr("r", function(d) {
                              return ridus(d.weight);
                          })
                          .attr("fill", function(d) {
                              return color(d.type);
                          })
                          .call(d3.drag()
                                  .on("start", dragstarted)
                                  .on("drag", dragged)
                                  .on("end", dragended));

        label = _selection.selectAll(".force-nodelabel")
                          .data(_data.nodes)
                          .enter()
                          .append("text")
                          .attr("class", "force-nodelabel")
                          .text(function(d) {
                              return d.name;
                          })
                          .style("text-anchor", "middle")
                          .style("fill", "#555")
                          .style("font-family", "Arial")
                          .style("font-size", 12);

        nodes.append("title").text(function(d) {
            return d.name;
        });

        simulation.nodes(_data.nodes).on("tick", ticked);

        simulation.force("link").links(_data.links);
        return instance;
    };

    instance.data = function(data, keyFunction, sumFunction) {
        if (!arguments.length) {
            return _data;
        }
        console.log(data);
        _data = data;
        return instance;
    };

    instance.call = function(callBack) {
        _selection.call(callBack);
        return instance;
    };

    instance.nodesCall = function(callBack) {
        _selection.selectAll(".force-nodes").call(callBack);
        _selection.selectAll(".force-nodelabel").call(callBack);
        return instance;
    };
    instance.linksCall = function(callBack) {
        _selection.selectAll(".force-links").call(callBack);
        return instance;
    };
    instance.height = function(h) {
        if (!arguments.length) {
            return _height;
        }
        _height = h;
        return instance;
    };

    instance.width = function(w) {
        if (!arguments.length) {
            return _width;
        }
        _width = w;
        return instance;
    };

    instance.margin = function(position, value) {
        if (position !== undefined) {
            position = position.toLowerCase();
        }
        if (arguments.length == 2) {
            if (_margin[position]) {
                _margin[position] = value;
            }
        } else if (arguments.length == 1) {
            return _margin[position];
        }

        return instance;
    }

    function ticked() {
        links.attr("x1", function(d) {
            return d.source.x;
        }).attr("y1", function(d) {
            return d.source.y;
        }).attr("x2", function(d) {
            return d.target.x;
        }).attr("y2", function(d) {
            return d.target.y;
        });

        nodes.attr("cx", function(d) {
            var radius = ridus(d.weight);
            return d.x = Math.max(radius, Math.min(width - radius, d.x));
        }).attr("cy", function(d) {
            var radius = ridus(d.weight);
            return d.y = Math.max(radius, Math.min(height - radius, d.y));
        });

        label.attr("x", function(d) {
            return d.x;
        }).attr("y", function(d) {
            return d.y;
        });
    }
    function dragstarted(d) {
        if (!d3.event.active)
            simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
    }

    function dragged(d) {
        d.fx = d3.event.x;
        d.fy = d3.event.y;
    }

    function dragended(d) {
        if (!d3.event.active)
            simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    }
    return instance;
}

function Pack(rootContainer) {
    console.log(rootContainer);
    var instance = {};
    var _width, _height, _bodyWidth, _bodyHeight;
    var _margin = {
        top    : 20,
        right  : 20,
        bottom : 20,
        left   : 20
    };
    var _data, _selection;
    var color = d3.scaleOrdinal(d3.schemeCategory20c);

    var init = function(container) {
        _selection = (container instanceof d3.selection) ? container : d3.select(container);
        _width = parseInt((_width = _selection.attr("width")) ? _width : _selection.style("width"));
        _height = parseInt((_height = _selection.attr("height")) ? _height : _selection.style("height"));
        _selection = _selection.append("svg")
                               .attr("width", _width)
                               .attr("height", _height)
                               .style("top", _margin.top + "px")
                               .style("left", _margin.left + "px")
                               .append("g")
                               .attr("width", _width)
                               .attr("height", _height)
                               .attr("class", "graphSvg");

    };

    var computeBoby = function() {
        _bodyWidth = (_width - _margin.left - _margin.right);
        _bodyHeight = (_height - _margin.top - _margin.bottom);
    };

    if (rootContainer) {
        init(rootContainer);
    }

    instance.render = function(container) {
        if (container !== undefined) {
            init(container);
        }
        computeBoby();
        console.log(_data);

        var pack = d3.pack().size([width, height]).padding(5);
        var node = _selection.selectAll(".packnode")
                             .data(pack(_data).descendants())
                             .enter()
                             .append("g")
                             .attr("class", "packnode")
                             .attr("transform", function(d) {
                                 return "translate(" + d.x + "," + d.y + ")";
                             });
        node.append("circle")
            .attr("r", function(d) {
                return d.r;
            })
            .style("fill", "rgb(31, 119, 180)")
            .style("fill-opacity", "0.25")
            .style("stroke", "rgb(31, 119, 180)")
            .style("stroke-width", "1px");

        node.append("title").text(function(d) {
            return d.data.name + "\n";
        });

        node.filter(function(d) {
            return !d.children;
        }).append("text").attr("dy", "0.3em").text(function(d) {
            return d.data.name.substring(0, d.r / 3);
        }).style("font", "10px sans-serif").style("text-anchor", "middle");

        return instance;
    };

    instance.data = function(data, keyFunction, sumFunction) {
        if (!arguments.length) {
            return _data;
        }
        console.log(data);
        _data = d3.hierarchy(data).sum(function(d) {
            return d.weight;
        }).sort(function(a, b) {
            return b.weight - a.weight;
        });
        console.log(_data);
        return instance;
    };

    instance.call = function(callBack) {
        _selection.selectAll(".treeMapNode").call(callBack);
        return instance;
    };

    instance.height = function(h) {
        if (!arguments.length) {
            return _height;
        }
        _height = h;
        return instance;
    };

    instance.width = function(w) {
        if (!arguments.length) {
            return _width;
        }
        _width = w;
        return instance;
    };

    instance.margin = function(position, value) {
        if (position !== undefined) {
            position = position.toLowerCase();
        }
        if (arguments.length == 2) {
            if (_margin[position]) {
                _margin[position] = value;
            }
        } else if (arguments.length == 1) {
            return _margin[position];
        }

        return instance;
    }

    return instance;

}

function LineChart(rootContainer) {
    console.log(rootContainer);
    var instance = {};
    var _width, _height, _bodyWidth, _bodyHeight;
    var _margin = {
        top    : 20,
        right  : 20,
        bottom : 100,
        left   : 150
    };
    var _data, _selection;
    var color = d3.scaleOrdinal(d3.schemeCategory20c);
    var xValue, yValue, line;

    var init = function(container) {
        _selection = (container instanceof d3.selection) ? container : d3.select(container);
        _width = parseInt((_width = _selection.attr("width")) ? _width : _selection.style("width"));
        _height = parseInt((_height = _selection.attr("height")) ? _height : _selection.style("height"));
        _selection = _selection.append("svg")
                               .attr("width", _width)
                               .attr("height", _height)
                               .append("g")
                               .attr("transform", "translate(" + _margin.left + "," + _margin.top + ")")
                               .attr("width", _width)
                               .attr("height", _height)
                               .attr("class", "graphSvg");

    };

    var computeBoby = function() {
        _bodyWidth = (_width - _margin.left - _margin.right) * 2 / 3;
        _bodyHeight = (_height - _margin.top - _margin.bottom) * 2 / 3;
    };

    if (rootContainer) {
        init(rootContainer);
    }

    instance.render = function(container) {
        if (container !== undefined) {
            init(container);
        }
        computeBoby();

        xValue = d3.scaleTime().range([0, _bodyWidth]);
        yValue = d3.scaleLinear().range([_bodyHeight, 0]);

        line = d3.line().x(function(d) {
            return xValue(d.date);
        }).y(function(d) {
            return yValue(d.weight);
        });

        xValue.domain(d3.extent(_data, function(d) {
            return d.date;
        }));
        yValue.domain([0, d3.max(_data, function(d) {
            return d.weight;
        })]);

        // Add the valueline path.
        _selection.append("path")
                  .data([_data])
                  .attr("class", "line")
                  .style("fill", "none")
                  .style("stroke", "steelblue")
                  .style("stroke-width", "2px")
                  .attr("d", line);

        // Add the X Axis
        _selection.append("g")
                  .attr("transform", "translate(0," + _bodyHeight + ")")
                  .call(d3.axisBottom(xValue));

        // Add the Y Axis
        _selection.append("g").call(d3.axisLeft(yValue));

        return instance;
    };

    instance.data = function(data) {
        if (!arguments.length) {
            return _data;
        }
        _data = data;
        // format the data
        var parseTime = d3.timeParse("%d-%b-%y");
        _data.forEach(function(d) {
            d.date = parseTime(d.name);
            d.weight = +d.weight;
        });
        return instance;
    };

    instance.call = function(callBack) {
        _selection.selectAll("*").call(callBack);
        return instance;
    };

    instance.height = function(h) {
        if (!arguments.length) {
            return _height;
        }
        _height = h;
        return instance;
    };

    instance.width = function(w) {
        if (!arguments.length) {
            return _width;
        }
        _width = w;
        return instance;
    };

    instance.margin = function(position, value) {
        if (position !== undefined) {
            position = position.toLowerCase();
        }
        if (arguments.length == 2) {
            if (_margin[position]) {
                _margin[position] = value;
            }
        } else if (arguments.length == 1) {
            return _margin[position];
        }

        return instance;
    }

    return instance;

}

function BarChart(rootContainer) {
    console.log(rootContainer);
    var instance = {};
    var _width, _height, _bodyWidth, _bodyHeight;
    var _margin = {
        top    : 20,
        right  : 20,
        bottom : 100,
        left   : 150
    };
    var _data, _selection;
    var color = d3.scaleOrdinal(d3.schemeCategory20c);
    var xValue, yValue, line;

    var init = function(container) {
        _selection = (container instanceof d3.selection) ? container : d3.select(container);
        _width = parseInt((_width = _selection.attr("width")) ? _width : _selection.style("width"));
        _height = parseInt((_height = _selection.attr("height")) ? _height : _selection.style("height"));
        _selection = _selection.append("svg")
                               .attr("width", _width)
                               .attr("height", _height)
                               .append("g")
                               .attr("transform", "translate(" + _margin.left + "," + _margin.top + ")")
                               .attr("width", _width)
                               .attr("height", _height)
                               .attr("class", "graphSvg");

    };

    var computeBoby = function() {
        _bodyWidth = (_width - _margin.left - _margin.right) * 2 / 3;
        _bodyHeight = (_height - _margin.top - _margin.bottom) * 2 / 3;
    };

    if (rootContainer) {
        init(rootContainer);
    }

    instance.render = function(container) {
        if (container !== undefined) {
            init(container);
        }
        computeBoby();

        xValue = d3.scaleBand().range([0, _bodyWidth]);
        yValue = d3.scaleLinear().range([_bodyHeight, 0]);

        xValue.domain(_data.map(function(d) {
            return d.name;
        }));
        yValue.domain([0, d3.max(_data, function(d) {
            return d.weight;
        })]);

        // append the rectangles for the bar chart
        _selection.selectAll(".bar")
                  .data(_data)
                  .enter()
                  .append("rect")
                  .attr("class", "bar")
                  .attr("x", function(d) {
                      return xValue(d.name);
                  })
                  .attr("width", xValue.bandwidth())
                  .attr("y", function(d) {
                      return yValue(d.weight);
                  })
                  .attr("height", function(d) {
                      return _bodyHeight - yValue(d.weight);
                  })
                  .style("fill", "steelblue")
                  .on("mouseover", function(d) {
                      console.log(d);
                      var currColor = d3.color("steelblue");
                      currColor.opacity = 0.6;
                      d3.select(this)
                        .style("fill", d3.rgb(currColor))
                        .style("cursor", "pointer");
                  })
                  .on('mouseout', function(d) {
                      d3.select(this)
                        .style("fill", d3.rgb("steelblue"))
                        .style("cursor", "pointer");
                  });

        // Add the X Axis
        _selection.append("g")
                  .attr("transform", "translate(0," + _bodyHeight + ")")
                  .call(d3.axisBottom(xValue));

        // Add the Y Axis
        _selection.append("g").call(d3.axisLeft(yValue));

        return instance;
    };

    instance.data = function(data) {
        if (!arguments.length) {
            return _data;
        }
        _data = data;
        // format the data
        var parseTime = d3.timeParse("%d-%b-%y");
        _data.forEach(function(d) {
            d.date = parseTime(d.name);
            d.weight = +d.weight;
        });
        return instance;
    };

    instance.call = function(callBack) {
        _selection.selectAll("*").call(callBack);
        return instance;
    };

    instance.height = function(h) {
        if (!arguments.length) {
            return _height;
        }
        _height = h;
        return instance;
    };

    instance.width = function(w) {
        if (!arguments.length) {
            return _width;
        }
        _width = w;
        return instance;
    };

    instance.margin = function(position, value) {
        if (position !== undefined) {
            position = position.toLowerCase();
        }
        if (arguments.length == 2) {
            if (_margin[position]) {
                _margin[position] = value;
            }
        } else if (arguments.length == 1) {
            return _margin[position];
        }

        return instance;
    }

    return instance;
}

function Scatterplot(rootContainer) {
    console.log(rootContainer);
    var instance = {};
    var _width, _height, _bodyWidth, _bodyHeight;
    var _margin = {
        top    : 50,
        right  : 20,
        bottom : 100,
        left   : 150
    };
    var _data, _selection;
    var color = d3.scaleOrdinal(d3.schemeCategory20c);
    var xValue, yValue, line;

    var init = function(container) {
        _selection = (container instanceof d3.selection) ? container : d3.select(container);
        _width = parseInt((_width = _selection.attr("width")) ? _width : _selection.style("width"));
        _height = parseInt((_height = _selection.attr("height")) ? _height : _selection.style("height"));
        _selection = _selection.append("svg")
                               .attr("width", _width)
                               .attr("height", _height)
                               .append("g")
                               .attr("transform", "translate(" + _margin.left + "," + _margin.top + ")")
                               .attr("width", _width)
                               .attr("height", _height)
                               .attr("class", "graphSvg");

    };

    var computeBoby = function() {
        _bodyWidth = (_width - _margin.left - _margin.right) * 2 / 3;
        _bodyHeight = (_height - _margin.top - _margin.bottom) * 2 / 3;
    };

    if (rootContainer) {
        init(rootContainer);
    }

    instance.render = function(container) {
        if (container !== undefined) {
            init(container);
        }
        computeBoby();

        xValue = d3.scaleBand().range([0, _bodyWidth]);
        yValue = d3.scaleLinear().range([_bodyHeight, 0]);

        xValue.domain(_data.map(function(d) {
            return d.name;
        }));
        yValue.domain([0, d3.max(_data, function(d) {
            return d.weight;
        })]);
        var ridus = d3.scaleLinear()
                      .range([10, 20])
                      .domain([d3.min(_data, function(d) {
                          return d.weight;
                      }), d3.max(_data, function(d) {
                          return d.weight;
                      })]);

        _selection.selectAll("dot")
                  .data(_data)
                  .enter()
                  .append("circle")
                  .attr("r", function(d) {
                      return ridus(d.weight);
                  })
                  .attr("cx", function(d) {
                      return xValue(d.name);
                  })
                  .attr("cy", function(d) {
                      return yValue(d.weight);
                  })
                  .style("fill", function(d) {
                      return color(d.name);
                  })
                  .on("mouseover", function(d) {
                      console.log(d);
                      var currColor = d3.color(color(d.name));
                      currColor.opacity = 0.6;
                      d3.select(this)
                        .style("fill", d3.rgb(currColor))
                        .style("cursor", "pointer")
                        .attr("r", function(d) {
                            return ridus(d.weight * 1.5);
                        });
                  })
                  .on('mouseout', function(d) {
                      d3.select(this)
                        .style("fill", d3.rgb(color(d.name)))
                        .style("cursor", "pointer")
                        .attr("r", function(d) {
                            return ridus(d.weight);
                        });
                  });

        // Add the X Axis
        _selection.append("g")
                  .attr("transform", "translate(0," + _bodyHeight + ")")
                  .call(d3.axisBottom(xValue));

        // Add the Y Axis
        _selection.append("g").call(d3.axisLeft(yValue));

        return instance;
    };

    instance.data = function(data) {
        if (!arguments.length) {
            return _data;
        }
        _data = data;
        // format the data
        var parseTime = d3.timeParse("%d-%b-%y");
        _data.forEach(function(d) {
            d.date = parseTime(d.name);
            d.weight = +d.weight;
        });
        return instance;
    };

    instance.call = function(callBack) {
        _selection.selectAll("*").call(callBack);
        return instance;
    };

    instance.height = function(h) {
        if (!arguments.length) {
            return _height;
        }
        _height = h;
        return instance;
    };

    instance.width = function(w) {
        if (!arguments.length) {
            return _width;
        }
        _width = w;
        return instance;
    };

    instance.margin = function(position, value) {
        if (position !== undefined) {
            position = position.toLowerCase();
        }
        if (arguments.length == 2) {
            if (_margin[position]) {
                _margin[position] = value;
            }
        } else if (arguments.length == 1) {
            return _margin[position];
        }

        return instance;
    }

    return instance;
}

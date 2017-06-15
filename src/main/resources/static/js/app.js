var localCache = window.sessionStorage;
var mapCacheMaxSize = null; // TODO just using d3.map to cache data
if (!localCache) {
    console.warn("the browser no support localStorage, no cache data.");
}

const containerWidth = document.body.clientWidth;
// the height of title is 35px
const containerHeight = document.body.clientHeight - 35;
const margin = {
    top    : 35,
    right  : 10,
    bottom : 10,
    left   : 10
};
const width = containerWidth - margin.left - margin.right;
const height = containerHeight - margin.top - margin.bottom;
const color = d3.scaleOrdinal(d3.schemeCategory20c);
const displayTransition = d3.transition().duration(1000).ease(d3.easeCircleIn);

const menuWidth = width * 2 / 3;
const menuHeight = height * 3 / 4;

d3.select(".view-content")
  .style("width", containerWidth + "px")
  .style("height", containerHeight + "px")
  .style("position", "relative");

const viewMenu = d3.select(".view-menu")
                   .style("width", containerWidth + "px")
                   .style("height", containerHeight + "px")
                   .style("line-height", containerHeight + "px")
                   .style("text-align", "center")
                   .style("vertical-align", "middle")
                   .append("div")
                   .attr("class", "menu-items")
                   .style("position", "relative")
                   .style("display", "inline-block")
                   .style("width", menuWidth + "px")
                   .style("height", menuHeight + "px")
                   .style("top", margin.top + "px");

var viewMenuDisplay = {
    hide : function() {
        viewMenu.transition(displayTransition).style("display", "none");
    },
    show : function() {
        viewMenu.transition(displayTransition).style("display", "inline-block");
    }
}

var graphContainer = d3.select(".graphContainer")
                       .style("width", width + "px")
                       .style("height", height + "px")
                       .style("display", "none")
                       .style("position", "absolute")
                       .style("top", margin.top + "px");

var graphDisplay = {
    hide : function() {
        graphContainer.transition(displayTransition).style("display", "none");
    },
    show : function() {
        if (graphContainer.style("display") == "none") {
            graphContainer.selectAll("*").remove();
            graphContainer.style("display", "inline-block");
        }
    }
}

loadViewMenu();

function loadViewMenu() {
    var ALLVIEWNAMECACHENAME = "VIEWNAMES";
    var viewNames = null;
    if (localCache) {
        console.log("load all view names from local sessionStorage...");
        viewNames = JSON.parse(localCache.getItem(ALLVIEWNAMECACHENAME));
    }

    if (viewNames == null) {
        var parameter = "";
        d3.request("/view/allViewNames")
          .header("Content-Type", "application/x-www-form-urlencoded")
          .post(parameter, drawMenu);
    } else {
        drawMenu(null, viewNames);
    }

    function drawMenu(error, result) {
        if (error) {
            throw error;
        }
        var viewNamesJsonData = result;
        if (!(result.responseText === undefined)) {
            // parse data from request result, and cache it
            viewNamesJsonData = JSON.parse(result.responseText);
            if (localCache) {
                console.log("cache all view names to local sessionStorage...");
                localCache.setItem(ALLVIEWNAMECACHENAME, JSON.stringify(viewNamesJsonData));
            }
        }
        TreeMap(viewMenu).data(viewNamesJsonData)
                         .render()
                         .call(viewMenuClickEvent);

        function viewMenuClickEvent(selection) {
            selection.on("click", function(d) {
                console.log(selection);
                var currColor = d3.color(color(d.data.name));
                currColor.opacity = 1;
                d3.select(this).style("background", d3.rgb(currColor))
                viewMenuDisplay.hide();
                d3.select("#view-home").style("display", "inline-block");
                d3.select("#current-view")
                  .attr("value", d3.select(this).text())
                  .text("\\ " + d3.select(this).text());
                drawViewGraph(d3.select(this).text());
            });
            d3.select("#view-home").on("click", function() {
                d3.select("#current-view").text("");
                d3.select("#view-home").style("display", "none");
                graphDisplay.hide();
                viewMenuDisplay.show();
            });
        }
    }

}

function drawViewGraph(viewName) {
    var viewData = null;
    if (localCache) {
        viewData = JSON.parse(localCache.getItem(viewName));
    }

    if (viewData != null) {
        drawing(null, viewData);
    } else {
        var parameter = "viewname=" + viewName;
        console.log("fetch data for view:" + viewName);
        d3.request("/view/data")
          .header("Content-Type", "application/x-www-form-urlencoded")
          .post(parameter, drawing);
    }
}

function drawing(error, data) {
    if (error) {
        console.log(error)
        throw error;
    }

    graphDisplay.show();

    var viewJsonData = data;
    if (!(data.responseText === undefined)) {
        // parse data from request result, and cache it
        viewJsonData = JSON.parse(data.responseText);
        if (localCache) {
            localCache.setItem(viewJsonData.viewName, JSON.stringify(viewJsonData));
        }
    }
    const chartType = viewJsonData.chartType;
    switch (chartType) {
        case "TREEMAP" :
            drawTreeMap(viewJsonData.data, graphContainer, width, height, drawTreeMapCallback);
            break;
        case "FORCE" :
            Force(graphContainer).data(viewJsonData.data).render();
            // .nodesCall(function(selection) {
            // selection.on("contextmenu", function() {
            // d3.event.preventDefault();
            // clickMenu.show();
            // });
            // d3.select(".view-content").on("click", function() {
            // var event = d3.event;
            // console.log(event);
            // clickMenu.hide();
            // });
            // });
            break;
        case "PIE" :
            break;
        case "TREE" :
            break;
        case "PACK" :
            Pack(graphContainer).data(viewJsonData.data).render();
            break;
        case "BUNDLE" :
            break;
        case "CHORT" :
            break;
        case "BAR" :
            BarChart(graphContainer).data(viewJsonData.data).render();
            break;
        case "HISTOGRAM" :
            break;
        case "LINE" :
            LineChart(graphContainer).data(viewJsonData.data).render();
            break;
        case "SCATTERPLOT" :
            Scatterplot(graphContainer).data(viewJsonData.data).render();
            break;
        default :
            unSupportChartType(viewJsonData);
    }
    function unSupportChartType(viewData) {
        console.log("unsupport chart type: " + viewData.chartType);
        console.log(viewData);
    }

}

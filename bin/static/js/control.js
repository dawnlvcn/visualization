updateDbmsTypes();
updateAllDsNames();

console.log(d3.select("#filterDefault").property('value'));
var AllDataSourceCache = {};
function updateAllDsNames() {
    d3.request("/datasource/all")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .post("username=" + d3.select("#username").text(), function(error, result) {
          if (error) {
              throw error;
          }
          var allDS = JSON.parse(result.responseText);
          // cache
          AllDataSourceCache = allDS;

          var dsOptions = d3.select("#select-ds");
          const theSelectedOption = dsOptions.property('value');
          dsOptions.selectAll("option[data-dsname]").remove();
          for (var index in allDS) {
              dsOptions.append("option")
                       .attr("value", allDS[index].dsName)
                       .attr("data-dsname", allDS[index].dsName)
                       .text(allDS[index].dsName);
          }

          // bind datasource
          dsOptions.on("change", updateDsSource);

          dsOptions.selectAll("option").attr("selected", "false");
          if (theSelectedOption != "") {
              console.log(theSelectedOption);
              dsOptions.select("option[value=" + theSelectedOption + "]")
                       .attr("selected", "true");
              dsOptions.dispatch("change");
          } else {
              dsOptions.select("option[value=" + allDS[index].dsName + "]")
                       .attr("selected", "true");
              console.log(allDS[index].dsName);
              dsOptions.dispatch("change");
          }
          
      });
}

function updateDsSource() {
	loadViewMenu();
    var currentDSName = d3.select("#select-ds").property("value");
    var currentDS = AllDataSourceCache[currentDSName];
    console.log(currentDS);
    d3.select("#select-dbms-type")
      .select("option[value=" + currentDS.dbmsType + "]")
      .attr("selected", "true");
    d3.select("#ds-name").property("value", currentDSName);
    d3.select("#ds-url").property("value", currentDS.dbUrl);
    d3.select("#ds-user").property("value", currentDS.dbUser);
    d3.select("#ds-pass").property("value", "     ");
}

function clearDsInputTexts() {
    d3.select("#ds-name").property("value", "");
    d3.select("#ds-url").property("value", "");
    d3.select("#ds-user").property("value", "");
    d3.select("#ds-pass").property("value", "");
}

function updateDbmsTypes() {
    d3.request("/datasource/dbms")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .post("", function(error, result) {
          d3.select("#select-dbms-type").selectAll('option').remove();
          d3.select("#select-dbms-type")
            .selectAll('option')
            .data(JSON.parse(result.responseText))
            .enter()
            .append('option')
            .property("value", function(d) {
                console.log(d);
                return d;
            })
            .text(function(d) {
                return d;
            });
      });
}

d3.select("#edit").on("click", function() {
    updateDsSource();
    d3.select("#edit").style("opacity", 0);
    d3.select(".config-datasource").style("display", "inline-block");
});

d3.select(".close-ds-config").on("click", function() {
    d3.select("#edit").style("opacity", 1);
    d3.select(".config-datasource").style("display", "none");
    clearDsInputTexts();
});

d3.select("#ds-delete").on("click", function() {
    var parameter = "username=" + d3.select("#username").text();
    parameter += "&dsname=" + d3.select("#ds-name").property("value");
    parameter += "&dbmstype=" + d3.select("#select-dbms-type")
                                  .property("value");
    d3.request("/datasource/delete")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .post(parameter, function(error, result) {
          if (error) {
              alert("failed");
              throw error;
          }
          if (result.responseText.toUpperCase() == "TRUE") {
              alert("delete success");
              updateAllDsNames();
          } else {
              alert("delete failed");
          }
      });

});
d3.select("#ds-new").on("click", function() {
    console.log(d3.select("#ds-new").text());
    if (d3.select("#ds-new").text() == 'new') {
        d3.selectAll(".for-new-ds").style("display", "inline-block");
        d3.select("#ds-new").text("cancel");
        d3.select("#ds-save").text("save");
        clearDsInputTexts();
    } else {
        d3.selectAll(".for-new-ds").style("display", "none");
        d3.select("#ds-new").text("new");
        d3.select("#ds-save").text("update");
        updateDsSource();
    }
});

d3.select("#ds-user").on("input", function() {
    var pass = d3.select("#ds-pass");
    console.log(pass.property("value").trim());
    if (pass.property("value").trim() == "") {
        pass.property("value", "");
    }
});

d3.select("#ds-test").on("click", function() {
    d3.request("/datasource/test")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .post(wrapDatasource(), function(error, result) {
          if (error) {
              alert("failed");
              throw error;
          }
          if (result.responseText.toUpperCase() == "TRUE") {
              alert("success");
          } else {
              alert("failed");
          }
      });
});

d3.select("#ds-save").on("click", function() {
    var parameter = wrapDatasource();
    var isUpdate = d3.select("#ds-save").text() == 'update';
    if (isUpdate) {
        parameter += "&update=true";
    }
    d3.request("/datasource/save")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .post(parameter, function(error, result) {
          if (error) {
              alert("failed");
              throw error;
          }
          if (result.responseText.toUpperCase() == "TRUE") {
              updateAllDsNames();
              if (isUpdate) {
                  alert("success update");
              } else {
                  alert("success save");
              }
          } else {
              if (isUpdate) {
                  alert("failed update");
              } else {
                  alert("failed save");
              }
          }
      });

});

function wrapDatasource() {
    var parameter = "username=" + d3.select("#username").text();
    parameter += "&dbmstype=" + d3.select("#select-dbms-type")
                                  .property("value");
    parameter += "&dsname=" + d3.select("#ds-name").property("value");
    parameter += "&dburl=" + d3.select("#ds-url").property("value");
    parameter += "&dbuser=" + d3.select("#ds-user").property("value");
    parameter += "&dbpass=" + d3.select("#ds-pass").property("value");
    return parameter;
}

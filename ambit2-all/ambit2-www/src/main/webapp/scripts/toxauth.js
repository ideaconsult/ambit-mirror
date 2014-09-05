/* toxauth.js - Several kits for querying/manipulating authorization-related stuff: policies, roles, users
 *
 * Copyright 2012-2014, IDEAconsult Ltd. http://www.ideaconsult.net/
 * Created by Ivan Georgiev
**/

/* jToxPolicy - manages connection between user roles and permitted actions on specified services
*/
var jToxPolicy = (function () {
  var defaultSettings = { // all settings, specific for the kit, with their defaults. These got merged with general (jToxKit) ones.
    loadOnInit: false,      // whether to make initial load upon initializing the kit
    noInterface: false,     // whether to have interface... at all.
    sDom: "rt",
    oLanguage: {
      "sLoadingRecords": "No policies found.",
      "sZeroRecords": "No policies found.",
      "sEmptyTable": "No policies available.",
      "sInfo": "Showing _TOTAL_ policy(s) (_START_ to _END_)"
    },
    onRow: function (row, data, index) {
      jT.$('select', row).each(function () {
        $(this).val(data[$(this).data('data')]);
      });
    },
    configuration: { 
      columns : {
        policy: {
          'Id': { iOrder: 0, sClass: "center", sDefaultContent: '', bSortable: false, sTitle: "Id", mData: "uri", sWidth: "50px", mRender: function (data, type, full) {
            if (type == 'sort')
              return !!data ? '0' : '1';
            else if (type != 'display')
              return data || '';
            else if (!data)
              return '<span class="ui-icon ui-icon-plusthick jtox-handler jtox-inline jtox-hidden" data-handler="add"></span>';
            else
              return '<span class="ui-icon ui-icon-closethick jtox-handler jtox-inline" data-handler="remove"></span>';
          }},
          'Role': { iOrder: 1, sTitle: "Role", sDefaultContent: '', sWidth: "20%", mData: "role"},
          'Service': {iOrder: 2, sTitle: "Service", sDefaultContent: '', mData: "resource", sWidth: "40%", mRender: jT.ui.inlineChanger('resource', 'text', 'Service_') },
          'Get': { iOrder: 3, sClass: "center", sTitle: "Get", bSortable: false, sDefaultContent: false, mData: "methods.get", mRender: jT.ui.inlineChanger('methods.get', 'checkbox') },
          'Post': { iOrder: 4, sClass: "center", sTitle: "Post", bSortable: false, sDefaultContent: false, mData: "methods.post", mRender: jT.ui.inlineChanger('methods.post', 'checkbox') },
          'Put': { iOrder: 5, sClass: "center", sTitle: "Put", bSortable: false, sDefaultContent: false, mData: "methods.put", mRender: jT.ui.inlineChanger('methods.put', 'checkbox') },
          'Delete': { iOrder: 6, sClass: "center", sTitle: "Delete", bSortable: false, sDefaultContent: false, mData: "methods.delete", mRender: jT.ui.inlineChanger('methods.delete', 'checkbox') },
        }
      }
    }
  };
  
  var cls = function (root, settings) {
    var self = this;
    self.rootElement = root;
    jT.$(root).addClass('jtox-toolkit'); // to make sure it is there even when manually initialized
    
    self.settings = jT.$.extend(true, {}, defaultSettings, jT.settings, settings);
    self.policies = null;
    
    if (!self.settings.noInterface)
      self.init(settings);
        
    // finally, wait a bit for everyone to get initialized and make a call, if asked to
    if (self.settings.loadOnInit)
      self.loadPolicies();
  };

  cls.prototype.init = function (settings) {
    var self = this;
    self.rootElement.appendChild(jT.getTemplate('#jtox-policy'));
    self.settings.configuration.columns.policy.Id.sTitle = '';
    self.settings.configuration.columns.policy.Role.mRender = function (data, type, full) {
      return type != 'display' ? (data || '') : 
        '<select class="jt-inlineaction jtox-handler" data-handler="changed" data-data="role" value="' + (data || '') + '">' + self.roleOptions + '</select>';
    };
    
    var alerter = function (el, icon, task) {
      var mess = (!!task ? task.error : "Unknown error");
      $(el).removeClass(icon).addClass('ui-icon-alert').attr('title', mess);
      setTimeout(function () {
        $(el).addClass(icon).removeClass('ui-icon-alert');
        $(el).removeAttr('title');
      }, 3500);
    };
    
    var dataEnumer = function (data) {
      var out = {};
      ccLib.enumObject(data, function (val, name) {
        out[name] = val;
      });
      return out;
    };
    
    self.settings.configuration.handlers = {
      changed: function (e) {
        var data = jT.ui.rowData(this);
        if (!!data.uri) {
          // Initiate a change in THIS field.
          var el = this;
          var myData = $(el).data('data');
          var myObj = {};
          ccLib.setJsonValue(myObj, myData, ccLib.getObjValue(el));

          $(el).addClass('loading');
          // now make the update call...
          jT.call(self, data.uri, { method: 'PUT', data: dataEnumer(myObj) }, function (task) {
            jT.pollTask(self, task, function (task) {
              $(el).removeClass('loading');
              if (!task || !!task.error) {
                ccLib.setObjValue(el, ccLib.getJsonValue(data, myData)); // i.e. revert the old value
                alerter(el, '', task);
              }
              else {
                // we need to update the value... in our internal 'policies' array...
                var idx = $(self.table).dataTable().fnGetPosition($(el).closest('tr')[0]);
                ccLib.setJsonValue(self.policies[idx], myData, ccLib.getObjValue(el));
              }
            });
          });
        }
        else {
          // collect and validate and react
          var row = $(this).closest('tr');
          var inline = jT.ui.rowInline(row);
          if (!inline.role || !inline.resource)
            $('span.ui-icon-plusthick', row).addClass('jtox-hidden');
          else
            $('span.ui-icon-plusthick', row).removeClass('jtox-hidden');
        }
      },
      remove: function (e) {
        if (!window.confirm("Do you really want to delete this policy?"))
          return;
        var el = this;
        var data = jT.ui.rowData(this);
        $(el).addClass('loading');
        jT.call(self, data.uri, { method: "DELETE" }, function (task, jhr) {
          jT.pollTask(self, task, function (task) {
            $(el).removeClass('loading');
            if (!task || !!task.error)
              alerter(el, 'ui-icon-closethick', task)
            else // i.e. - on success - reload them!
              self.loadPolicies();
          }, jhr);
        });
      },
      add: function (e) {
        var data = jT.ui.rowInline(this, jT.ui.rowData(this));
        delete data['uri'];
        var el = this;
        $(el).addClass('loading');
        jT.call(self, '/admin/restpolicy', { method: "POST", data: dataEnumer(data)}, function (task) {
          jT.pollTask(self, task, function (task) {
            $(el).removeClass('loading');
            if (!task || !!task.error)
              alerter(el, 'ui-icon-plusthick', task)
            else // i.e. - on success - reload them!
              self.loadPolicies();
          });
        });
      },
    };
    
    // again , so that changed defaults can be taken into account.
    self.settings.configuration = jT.$.extend(true, self.settings.configuration, settings.configuration);
    
    self.table = jT.ui.putTable(self, jT.$('table', self.rootElement)[0], 'policy', { "aaSortingFixed": [[0, 'asc']] });
  };
  
  cls.prototype.loadPolicies = function (force) {
    var self = this;
    $(self.table).dataTable().fnClearTable();
    
    // The real policy loader...
    var realLoader = function () {
      jT.call(self, '/admin/restpolicy', function (result) { 
        if (!!result) {
          self.policies = result.policy;  
          if (!self.settings.noInterface)
            $(self.table).dataTable().fnAddData(result.policy.concat({  }));
        }
      });
    };
    
    // we need to load roles first, if not before, or if forces to...
    if (!self.roles || force) {
      jT.call(self, '/admin/role', function (roles) {
        if (!!roles) {
          // remember and prepare roles for select presenting...
          self.roles = roles;
          if (!self.settings.noInterface) {
            var optList = '';
            for (var i = 0, rl = roles.roles.length; i < rl; ++i)
              optList += '<option>' + roles.roles[i] + '</option>';
            self.roleOptions = optList;
          }

          // and now - really load the list
          realLoader();
        }
      });
    }
    else
      realLoader();
  };
  
  cls.prototype.query = function () {
    this.loadPolicies();
  }
  
  return cls;
})();
  
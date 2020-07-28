package converter.ecomXslToJson.controller;

import converter.ecomXslToJson.logic.IXslToJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("ecom")
public class EcomXslToJSONController {

    private IXslToJson ecomXslToJson;

    public IXslToJson getEcomXslToJson() {
        return ecomXslToJson;
    }

    @Autowired
    @Qualifier(value = "ecomXslToJson")
    public void setEcomXslToJson(IXslToJson ecomXslToJson) {
        this.ecomXslToJson = ecomXslToJson;
    }

    @GetMapping
    public ResponseEntity status(@RequestParam(value = "sid") String sid) {
        return new ResponseEntity(ecomXslToJson.status(sid), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity run(@RequestParam(value = "sid") String sid, @RequestBody Map<String, Object> body) {
        return new ResponseEntity(ecomXslToJson.run(sid, body), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity controls() {
        return new ResponseEntity(ecomXslToJson.getControls(), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity stop(@RequestParam(value = "sid") String sid) {
        return new ResponseEntity(ecomXslToJson.stop(sid), HttpStatus.OK);
    }
}

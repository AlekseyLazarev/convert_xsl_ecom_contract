package converter.ecomXslToJson.controller;

import converter.ecomXslToJson.logic.EcomContractXslToJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class EcomContractXslToJSONController {
    private EcomContractXslToJson ecomContractXslToJson;

    @Autowired
    public void setEcomContractXslToJson(EcomContractXslToJson ecomContractXslToJson) {
        this.ecomContractXslToJson = ecomContractXslToJson;
    }

    @GetMapping
    public ResponseEntity status(@RequestParam(value = "sid") String sid) {
        return new ResponseEntity(this.ecomContractXslToJson.status(sid), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity run(@RequestParam(value = "sid") String sid, @RequestBody Map<String, Object> body) {
        return new ResponseEntity(this.ecomContractXslToJson.run(sid, body), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity controls() {
        return new ResponseEntity(this.ecomContractXslToJson.getControls(), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity stop(@RequestParam(value = "sid") String sid) {
        return new ResponseEntity(this.ecomContractXslToJson.stop(sid), HttpStatus.OK);
    }
}

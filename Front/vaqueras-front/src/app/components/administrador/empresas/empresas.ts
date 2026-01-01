import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmpresaService, Empresa } from '../../../services/empresa';

@Component({
    selector: 'app-empresas',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './empresas.html',
    styleUrl: './empresas.css'

})
export class Empresas implements OnInit {
    empresas: Empresa[] = [];
    empresaActual: Empresa = { nombre: '', activa: true, descripcion: ''};
    mostrarFormulario = false;
    modoEdicion = false;
    cargando = false;
    procesando = false;
    error = '';
    exito = '';

    constructor(private empresaService: EmpresaService) { }

    ngOnInit(): void {
        this.cargar();
    }

    cargar(): void {
        this.cargando = true;
        this.empresaService.obtenerTodas().subscribe({
            next: (res) => {
                if (res.success && res.data) {
                    this.empresas = res.data;
                }
                this.cargando = false;
            },
            error: () => { this.cargando = false; }
        });
    }

    abrirFormulario(): void {
        this.modoEdicion = false;
        this.mostrarFormulario = true;
        this.empresaActual = { nombre: '', activa: true, descripcion: ''};
    }

    editar(empresa: Empresa): void {
        this.modoEdicion = true;
        this.mostrarFormulario = true;
        this.empresaActual = { ...empresa };
    }

    cerrarFormulario(): void {
        this.mostrarFormulario = false;
        this.error = '';
    }

    guardar(): void {
        if (!this.empresaActual.nombre.trim()) {
            this.error = 'El nombre es obligatorio';
            return;
        }

        this.procesando = true;
        const obs = this.modoEdicion
            ? this.empresaService.actualizar(this.empresaActual.idEmpresa!, this.empresaActual)
            : this.empresaService.crear(this.empresaActual);

        obs.subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = this.modoEdicion ? 'Empresa actualizada' : 'Empresa creada';
                    this.cerrarFormulario();
                    this.cargar();
                    setTimeout(() => { this.exito = ''; }, 3000);
                } else {
                    this.error = res.message;
                }
                this.procesando = false;
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al guardar';
                this.procesando = false;
            }
        });
    }

    eliminar(empresa: Empresa): void {
        if (!confirm(`¿Eliminar ${empresa.nombre}?`)) return;

        this.empresaService.eliminar(empresa.idEmpresa!).subscribe({
            next: (res) => {
                if (res.success) {
                    this.exito = 'Empresa eliminada';
                    this.cargar();
                    setTimeout(() => { this.exito = ''; }, 3000);
                } else {
                    this.error = res.message;
                }
            },
            error: (err) => {
                this.error = err.error?.message || 'Error al eliminar';
            }
        });
    }
}